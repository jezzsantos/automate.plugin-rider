package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitVersionCompatibility;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.components.AlertLabel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class UpgradeToolkitDialog extends DialogWrapper {

    private final UpgradeToolkitDialogContext context;
    private JPanel contents;
    private JLabel fromVersionTitle;
    private JLabel fromVersion;
    private JLabel toVersionTitle;
    private JLabel toVersion;
    private AlertLabel descriptionTitle;

    public UpgradeToolkitDialog(@Nullable Project project, @NotNull UpgradeToolkitDialogContext context) {

        super(project);
        this.context = context;

        this.init();
        this.setTitle(AutomateBundle.message("dialog.UpgradeToolkit.Title"));
        this.initControls();
    }

    public UpgradeToolkitDialogContext getContext() {return this.context;}

    @Override
    protected @Nullable JComponent createCenterPanel() {

        return this.contents;
    }

    @Override
    protected void doOKAction() {

        switch (this.context.getRuntimeCompatibility()) {

            case COMPATIBLE -> {
                assert false;
            }
            case TOOLKIT_AHEADOF_MACHINE -> super.doCancelAction();
            case MACHINE_AHEADOF_TOOLKIT -> {
                if (this.context.isToolkitUpgradeable()) {
                    super.doOKAction();
                }
                else {
                    super.doCancelAction();
                }
            }
        }
    }

    private void initControls() {

        this.fromVersionTitle.setText(AutomateBundle.message("dialog.UpgradeToolkit.FromVersion.Title"));
        this.toVersionTitle.setText(AutomateBundle.message("dialog.UpgradeToolkit.ToVersion.Title"));
        this.fromVersion.setText(this.context.getFromVersion());
        this.toVersion.setText(String.format("%s %s", this.context.getToVersion(), AutomateBundle.message("dialog.UpgradeToolkit.ToVersion.SubTitle")));
        this.descriptionTitle.setType(AlertLabel.AlertLabelType.NONE);
        switch (this.context.getRuntimeCompatibility()) {

            case COMPATIBLE -> {
                assert false;
            }
            case MACHINE_AHEADOF_TOOLKIT -> {
                if (this.context.isToolkitUpgradeable()) {
                    this.descriptionTitle.setText(
                      AutomateBundle.message("dialog.UpgradeToolkit.Description.Incompatible.MachineAheadOfToolkit.Upgradeable.Message", AutomateConstants.ExecutableName));
                    this.descriptionTitle.setType(AlertLabel.AlertLabelType.WARNING);
                    this.setOKButtonText(AutomateBundle.message("dialog.UpgradeDraft.OKButton.Execute.Title"));
                }
                else {
                    this.descriptionTitle.setText(
                      AutomateBundle.message("dialog.UpgradeToolkit.Description.Incompatible.MachineAheadOfToolkit.Message", AutomateConstants.ExecutableName,
                                             this.context.getToolkitName(),
                                             this.context.getToVersion()));
                    this.descriptionTitle.setType(AlertLabel.AlertLabelType.ERROR);
                    this.setOKButtonText(AutomateBundle.message("dialog.UpgradeToolkit.OKButton.Abort.Title"));
                }
            }
            case TOOLKIT_AHEADOF_MACHINE -> {
                this.descriptionTitle.setText(AutomateBundle.message("dialog.UpgradeDraft.Description.Incompatible.ToolkitAheadOfMachine.Message", AutomateConstants.ExecutableName,
                                                                     this.context.getFromVersion()));
                this.descriptionTitle.setType(AlertLabel.AlertLabelType.ERROR);
                this.setOKButtonText(AutomateBundle.message("dialog.UpgradeToolkit.OKButton.Abort.Title"));
            }
        }

        this.contents.invalidate();
    }

    public static class UpgradeToolkitDialogContext {

        private final boolean toolkitIsUpgradeable;
        private final ToolkitVersionCompatibility compatibility;
        private final String toolkitName;

        public UpgradeToolkitDialogContext(@NotNull String toolkitName, ToolkitVersionCompatibility compatibility, boolean toolkitIsUpgradeable) {

            this.toolkitName = toolkitName;
            this.compatibility = compatibility;
            this.toolkitIsUpgradeable = toolkitIsUpgradeable;
        }

        @NotNull
        public String getFromVersion() {return this.compatibility.getRuntimeVersion().getPublished();}

        @NotNull
        public String getToVersion() {return this.compatibility.getRuntimeVersion().getInstalled();}

        public boolean isToolkitUpgradeable() {return this.toolkitIsUpgradeable;}

        public AutomateConstants.ToolkitRuntimeVersionCompatibility getRuntimeCompatibility() {return this.compatibility.getRuntimeCompatibility();}

        @NotNull
        public String getToolkitName() {return this.toolkitName;}
    }
}
