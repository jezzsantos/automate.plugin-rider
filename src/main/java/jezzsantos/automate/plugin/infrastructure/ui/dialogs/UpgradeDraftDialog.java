package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.ColoredSideBorder;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBList;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.AutomateIcons;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftUpgradeReport;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftUpgradeReportItem;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.infrastructure.ITaskRunner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.util.List;
import java.util.function.Function;

public class UpgradeDraftDialog extends DialogWrapper {

    private final UpgradeDraftDialogContext context;
    private JPanel contents;
    private JCheckBox force;
    private JLabel fromVersionTitle;
    private JLabel fromVersion;
    private JLabel toVersionTitle;
    private JLabel toVersion;
    private JLabel descriptionTitle;
    private JBList<ListItem> changes;
    private JScrollPane changesPane;

    public UpgradeDraftDialog(@Nullable Project project, @NotNull UpgradeDraftDialogContext context) {

        super(project);
        this.context = context;

        this.init();
        this.setTitle(AutomateBundle.message("dialog.UpgradeDraft.Title"));
        this.initControls();
    }

    public UpgradeDraftDialogContext getContext() {return this.context;}

    @Override
    protected @Nullable JComponent createCenterPanel() {

        return this.contents;
    }

    @Override
    public void doCancelAction() {

        if (this.context.getPhase() == UpgradePhase.BEFORE) {
            super.doCancelAction();
            return;
        }

        if (this.context.getPhase() == UpgradePhase.AFTER) {
            super.doOKAction();
        }
    }

    @Override
    protected void doOKAction() {

        if (this.context.getPhase() == UpgradePhase.BEFORE) {
            this.context.upgrade(this.force.isSelected());
            initControls();
            this.contents.invalidate();
            return;
        }

        if (this.context.getPhase() == UpgradePhase.AFTER) {
            super.doOKAction();
        }
    }

    private void initControls() {

        var phase = this.context.getPhase();
        var bruteForceRequired = this.context.getBruteForceRequired();
        this.fromVersionTitle.setText(AutomateBundle.message("dialog.UpgradeDraft.FromVersion.Title"));
        this.toVersionTitle.setText(AutomateBundle.message("dialog.UpgradeDraft.ToVersion.Title"));
        this.fromVersion.setText(this.context.getFromVersion());
        this.toVersion.setText(this.context.getToVersion());
        this.descriptionTitle.setIconTextGap(5);
        this.descriptionTitle.setBorder(null);
        this.descriptionTitle.setIcon(AutomateIcons.StatusSuccess);
        switch (phase) {

            case BEFORE -> {
                var description = bruteForceRequired
                  ? AutomateBundle.message("dialog.UpgradeDraft.Description.Execute.Major.Message")
                  : AutomateBundle.message("dialog.UpgradeDraft.Description.Execute.Minor.Message");
                this.descriptionTitle.setText("<html>" + description + "</html>");
                if (bruteForceRequired) {
                    this.descriptionTitle.setIcon(AutomateIcons.StatusWarning);
                    this.descriptionTitle.setBorder(new ColoredSideBorder(JBColor.YELLOW, JBColor.YELLOW, JBColor.YELLOW, JBColor.YELLOW, 1));
                }
                this.force.setVisible(true);
                this.force.setText(AutomateBundle.message("dialog.UpgradeDraft.Force.Title"));
                this.force.setSelected(bruteForceRequired);
                this.force.setEnabled(!bruteForceRequired);
                this.changesPane.setVisible(false);
                this.setOKButtonText(bruteForceRequired
                                       ? AutomateBundle.message("dialog.UpgradeDraft.OKButton.Execute.BruteForce.Title")
                                       : AutomateBundle.message("dialog.UpgradeDraft.OKButton.Execute.Force.Title"));
                this.setCancelButtonText(AutomateBundle.message("dialog.UpgradeDraft.CancelButton.Execute.Title"));
            }
            case AFTER -> {
                this.descriptionTitle.setText(AutomateBundle.message("dialog.UpgradeDraft.Description.Summary.Message"));
                this.force.setVisible(false);
                this.changesPane.setVisible(true);
                this.changes.setVisible(true);
                var model = new DefaultListModel<ListItem>();
                this.context.getChanges()
                  .forEach(change -> model.addElement(new ListItem(change)));
                this.changes.setModel(model);
                this.changes.setCellRenderer(new ColoredListCellRenderer<>() {
                    @Override
                    protected void customizeCellRenderer(@NotNull JList<? extends ListItem> list, ListItem value, int index, boolean selected, boolean hasFocus) {

                        setIcon(value.getType() == AutomateConstants.UpgradeLogType.ABORT
                                  ? AutomateIcons.StatusWarning
                                  : AutomateIcons.StatusSuccess);
                        append(value.getMessage(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, value.getType() == AutomateConstants.UpgradeLogType.ABORT
                          ? JBColor.RED
                          : JBColor.GREEN));
                    }
                });
                this.setOKButtonText(AutomateBundle.message("dialog.UpgradeDraft.OKButton.Summary.Title"));
                this.setCancelButtonText(AutomateBundle.message("dialog.UpgradeDraft.CancelButton.Summary.Title"));
            }
        }
    }

    enum UpgradePhase {
        BEFORE,
        AFTER
    }

    public static class ListItem {

        private final DraftUpgradeReportItem item;

        public ListItem(@NotNull DraftUpgradeReportItem item) {this.item = item;}

        @NotNull
        public String getMessage() {

            return this.item.getMessage();
        }

        @NotNull
        public AutomateConstants.UpgradeLogType getType() {return this.item.getType();}
    }

    public static class UpgradeDraftDialogContext {

        private final boolean bruteForceRequired;
        private final Function<UpgradeDraftDialogContext, DraftUpgradeReport> upgrader;
        private final ITaskRunner runner;
        private UpgradePhase phase;
        private String fromVersion;
        private String toVersion;
        private List<DraftUpgradeReportItem> changes;
        private boolean force;
        private boolean upgradeSucceeded;

        public UpgradeDraftDialogContext(@NotNull String fromVersion, @NotNull String toVersion, boolean bruteForceRequired,
                                         Function<UpgradeDraftDialogContext, DraftUpgradeReport> upgrader) {

            this(ITaskRunner.getInstance(), fromVersion, toVersion, bruteForceRequired, upgrader);
        }

        @TestOnly
        public UpgradeDraftDialogContext(@NotNull ITaskRunner runner, @NotNull String fromVersion, @NotNull String toVersion, boolean bruteForceRequired,
                                         Function<UpgradeDraftDialogContext, DraftUpgradeReport> upgrader) {

            this.runner = runner;
            this.phase = UpgradePhase.BEFORE;
            this.force = bruteForceRequired;
            this.bruteForceRequired = bruteForceRequired;
            this.fromVersion = fromVersion;
            this.toVersion = toVersion;
            this.upgrader = upgrader;
            this.changes = List.of();
            this.upgradeSucceeded = false;
        }

        public UpgradePhase getPhase() {return this.phase;}

        public boolean getForce() {return this.force;}

        public boolean getBruteForceRequired() {return this.bruteForceRequired;}

        @NotNull
        public String getFromVersion() {return this.fromVersion;}

        @NotNull
        public String getToVersion() {return this.toVersion;}

        public void upgrade(boolean force) {

            this.force = force;
            var report = Try.safely(() -> this.runner.runToCompletion(AutomateBundle.message("dialog.UpgradeDraft.Title"), () -> this.upgrader.apply(this)));
            this.upgradeSucceeded = report != null;
            if (this.upgradeSucceeded) {
                this.fromVersion = report.getOldVersion();
                this.toVersion = report.getNewVersion();
                this.changes = report.getChanges();
                this.phase = UpgradePhase.AFTER;
            }
        }

        @NotNull
        public List<DraftUpgradeReportItem> getChanges() {return this.changes;}

        public boolean isSuccess() {return this.upgradeSucceeded;}
    }
}
