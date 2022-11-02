package jezzsantos.automate.plugin.infrastructure.ui.dialogs.drafts;

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
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftVersionCompatibility;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.IContainer;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.infrastructure.ITaskRunner;
import jezzsantos.automate.plugin.infrastructure.ui.components.AlertLabel;
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
    private AlertLabel descriptionTitle;
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
            switch (this.context.getDraftCompatibility()) {

                case COMPATIBLE, DRAFT_AHEADOF_TOOLKIT -> {
                    assert false;
                }
                case TOOLKIT_AHEADOF_DRAFT -> super.doOKAction();
            }
        }
    }

    @Override
    protected void doOKAction() {

        if (this.context.getPhase() == UpgradePhase.BEFORE) {
            switch (this.context.getDraftCompatibility()) {

                case COMPATIBLE -> {
                    assert false;
                }
                case DRAFT_AHEADOF_TOOLKIT -> super.doCancelAction();
                case TOOLKIT_AHEADOF_DRAFT -> {
                    this.context.setForce(this.force.isSelected());
                    this.context.upgrade();
                    initControls();
                }
            }
            return;
        }

        if (this.context.getPhase() == UpgradePhase.AFTER) {
            switch (this.context.getDraftCompatibility()) {

                case COMPATIBLE, DRAFT_AHEADOF_TOOLKIT -> {
                    assert false;
                }
                case TOOLKIT_AHEADOF_DRAFT -> super.doOKAction();
            }
        }
    }

    private void initControls() {

        var phase = this.context.getPhase();
        var bruteForceRequired = this.context.getBruteForceRequired();
        this.fromVersionTitle.setText(AutomateBundle.message("dialog.UpgradeDraft.FromVersion.Title"));
        this.toVersionTitle.setText(AutomateBundle.message("dialog.UpgradeDraft.ToVersion.Title"));
        this.fromVersion.setText(this.context.getFromVersion());
        this.toVersion.setText(this.context.getToVersion());
        this.descriptionTitle.setType(AlertLabel.AlertLabelType.NONE);
        switch (phase) {

            case BEFORE -> {
                switch (this.context.getDraftCompatibility()) {
                    case COMPATIBLE -> {
                        assert false;
                    }

                    case DRAFT_AHEADOF_TOOLKIT -> {
                        this.descriptionTitle.setText(
                          AutomateBundle.message("dialog.UpgradeDraft.Description.Incompatible.DraftAheadOfToolkit.Message", this.context.getToolkitName(),
                                                 this.context.getFromVersion()));
                        this.descriptionTitle.setType(AlertLabel.AlertLabelType.ERROR);
                        this.force.setVisible(false);
                        this.changesPane.setVisible(false);
                        this.setOKButtonText(AutomateBundle.message("dialog.UpgradeDraft.OKButton.Abort.Title"));
                        this.setCancelButtonText(AutomateBundle.message("dialog.UpgradeDraft.CancelButton.Cancel.Title"));
                    }
                    case TOOLKIT_AHEADOF_DRAFT -> {
                        this.descriptionTitle.setText(bruteForceRequired
                                                        ? AutomateBundle.message("dialog.UpgradeDraft.Description.Execute.Major.Message")
                                                        : AutomateBundle.message("dialog.UpgradeDraft.Description.Execute.Minor.Message"));
                        if (bruteForceRequired) {
                            this.descriptionTitle.setType(AlertLabel.AlertLabelType.WARNING);
                        }
                        this.force.setVisible(true);
                        this.force.setText(AutomateBundle.message("dialog.UpgradeDraft.Force.Title"));
                        this.force.setSelected(this.context.getForce());
                        this.force.setEnabled(!bruteForceRequired);
                        this.changesPane.setVisible(false);
                        this.setOKButtonText(bruteForceRequired
                                               ? AutomateBundle.message("dialog.UpgradeDraft.OKButton.Execute.BruteForce.Title")
                                               : AutomateBundle.message("dialog.UpgradeDraft.OKButton.Execute.Force.Title"));
                        this.setCancelButtonText(AutomateBundle.message("dialog.UpgradeDraft.CancelButton.Cancel.Title"));
                    }
                }
            }
            case AFTER -> {
                switch (this.context.getDraftCompatibility()) {
                    case COMPATIBLE, DRAFT_AHEADOF_TOOLKIT -> {
                        assert false;
                    }

                    case TOOLKIT_AHEADOF_DRAFT -> {
                        this.descriptionTitle.setText(AutomateBundle.message("dialog.UpgradeDraft.Description.Summary.Message"));
                        this.descriptionTitle.setType(AlertLabel.AlertLabelType.SUCCESS);
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
                        this.setOKButtonText(AutomateBundle.message("dialog.UpgradeDraft.OKButton.Done.Title"));
                        this.setCancelButtonText(AutomateBundle.message("dialog.UpgradeDraft.CancelButton.Close.Title"));
                    }
                }
            }
        }
        this.contents.invalidate();
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
        private final DraftVersionCompatibility compatibility;
        private final String toolkitName;
        private UpgradePhase phase;
        private List<DraftUpgradeReportItem> changes;
        private boolean force;
        private boolean upgradeSucceeded;

        public UpgradeDraftDialogContext(@NotNull String toolkitName, @NotNull DraftVersionCompatibility compatibility,
                                         Function<UpgradeDraftDialogContext, DraftUpgradeReport> upgrader) {

            this(IContainer.getTaskRunner(), toolkitName, compatibility,
                 compatibility.isDraftIncompatible(), upgrader);
        }

        @TestOnly
        public UpgradeDraftDialogContext(@NotNull ITaskRunner runner, @NotNull String toolkitName, @NotNull DraftVersionCompatibility compatibility, boolean bruteForceRequired,
                                         Function<UpgradeDraftDialogContext, DraftUpgradeReport> upgrader) {

            this.runner = runner;
            this.toolkitName = toolkitName;
            this.compatibility = compatibility;
            this.phase = UpgradePhase.BEFORE;
            this.force = bruteForceRequired;
            this.bruteForceRequired = bruteForceRequired;
            this.upgrader = upgrader;
            this.changes = List.of();
            this.upgradeSucceeded = false;
        }

        public UpgradePhase getPhase() {return this.phase;}

        public boolean getForce() {return this.force;}

        public void setForce(boolean force) {

            this.force = force;
        }

        public boolean getBruteForceRequired() {return this.bruteForceRequired;}

        @NotNull
        public String getFromVersion() {return this.compatibility.getToolkitVersion().getPublished();}

        @NotNull
        public String getToVersion() {return this.compatibility.getToolkitVersion().getInstalled();}

        public void upgrade() {

            var report = Try.safely(() -> this.runner.runModal(AutomateBundle.message("dialog.UpgradeDraft.Title"), () -> this.upgrader.apply(this)));
            this.upgradeSucceeded = report != null;
            if (this.upgradeSucceeded) {
                this.changes = report.getChanges();
                this.phase = UpgradePhase.AFTER;
            }
        }

        @NotNull
        public List<DraftUpgradeReportItem> getChanges() {return this.changes;}

        public boolean isSuccess() {return this.upgradeSucceeded;}

        public AutomateConstants.DraftToolkitVersionCompatibility getDraftCompatibility() {return this.compatibility.getDraftCompatibility();}

        @NotNull
        public String getToolkitName() {return this.toolkitName;}
    }
}
