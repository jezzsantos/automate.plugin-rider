package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBList;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.AutomateIcons;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftUpgradeReport;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftUpgradeReportItem;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

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

    @SuppressWarnings("Convert2MethodRef")
    public UpgradeDraftDialog(@Nullable Project project, @NotNull UpgradeDraftDialogContext context) {

        super(project);
        this.context = context;

        this.init();

        this.fromVersionTitle.setText(AutomateBundle.message("dialog.UpgradeDraft.FromVersion.Title"));
        this.toVersionTitle.setText(AutomateBundle.message("dialog.UpgradeDraft.ToVersion.Title"));
        this.fromVersion.setText(this.context.getFromVersion());
        this.toVersion.setText(this.context.getToVersion());
        var stage = this.context.getStage();
        switch (stage) {

            case EXECUTE -> {
                this.setTitle(AutomateBundle.message("dialog.UpgradeDraft.Title"));
                var bruteForceRequired = context.getBruteForceRequired();
                var description = bruteForceRequired
                  ? AutomateBundle.message("dialog.UpgradeDraft.Description.Execute.Major.Message")
                  : AutomateBundle.message("dialog.UpgradeDraft.Description.Execute.Minor.Message");
                this.descriptionTitle.setText("<html>" + description + "</html>");
                this.descriptionTitle.setIcon(bruteForceRequired
                                                ? AutomateIcons.StatusWarning
                                                : AutomateIcons.StatusSuccess);
                this.force.setVisible(true);
                this.force.setText(AutomateBundle.message("dialog.UpgradeDraft.Force.Title"));
                this.force.setSelected(bruteForceRequired);
                this.force.setEnabled(!bruteForceRequired);
                this.changes.setVisible(false);
                this.setOKButtonText(bruteForceRequired
                                       ? AutomateBundle.message("dialog.UpgradeDraft.OKButton.Execute.BruteForce.Title")
                                       : AutomateBundle.message("dialog.UpgradeDraft.OKButton.Execute.Force.Title"));
                this.setCancelButtonText(AutomateBundle.message("dialog.UpgradeDraft.CancelButton.Execute.Title"));
            }
            case SUMMARY -> {
                this.setTitle(AutomateBundle.message("dialog.UpgradeDraft.Title"));
                var description = AutomateBundle.message("dialog.UpgradeDraft.Description.Summary.Message");
                this.descriptionTitle.setText("<html>" + description + "</html>");
                this.descriptionTitle.setIcon(AutomateIcons.StatusSuccess);
                this.force.setVisible(false);
                this.changes.setVisible(true);
                var model = new DefaultListModel<ListItem>();
                this.context.getChanges()
                  .forEach(change -> new ListItem(change));
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

    public UpgradeDraftDialogContext getContext() {return this.context;}

    @Override
    protected @Nullable JComponent createCenterPanel() {

        return this.contents;
    }

    @Override
    protected void doOKAction() {

        super.doOKAction();
        if (this.context.getStage() == DialogMode.EXECUTE) {
            this.context.setForce(this.force.isSelected());
        }
    }

    enum DialogMode {
        EXECUTE,
        SUMMARY
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

        private final DialogMode stage;
        private final boolean bruteForceRequired;
        private final String fromVersion;
        private final String toVersion;
        private final List<DraftUpgradeReportItem> changes;
        private boolean force;

        public UpgradeDraftDialogContext(@NotNull String fromVersion, @NotNull String toVersion, boolean bruteForceRequired) {

            this.stage = DialogMode.EXECUTE;
            this.force = bruteForceRequired;
            this.bruteForceRequired = bruteForceRequired;
            this.fromVersion = fromVersion;
            this.toVersion = toVersion;
            this.changes = List.of();
        }

        public UpgradeDraftDialogContext(DraftUpgradeReport report) {

            this.stage = DialogMode.SUMMARY;
            this.force = false;
            this.bruteForceRequired = false;
            this.fromVersion = report.getOldVersion();
            this.toVersion = report.getNewVersion();
            this.changes = report.getChanges();
        }

        public DialogMode getStage() {return this.stage;}

        public boolean getForce() {return this.force;}

        public void setForce(boolean force) {

            this.force = force;
        }

        public boolean getBruteForceRequired() {return this.bruteForceRequired;}

        @NotNull
        public String getFromVersion() {return this.fromVersion;}

        @NotNull
        public String getToVersion() {return this.toVersion;}

        @NotNull
        public List<DraftUpgradeReportItem> getChanges() {return this.changes;}
    }
}
