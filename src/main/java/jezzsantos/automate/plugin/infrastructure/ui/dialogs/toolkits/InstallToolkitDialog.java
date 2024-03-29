package jezzsantos.automate.plugin.infrastructure.ui.dialogs.toolkits;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.components.TextFieldWithBrowseButtonAndHint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.io.File;

public class InstallToolkitDialog extends DialogWrapper {

    private final InstallToolkitDialogContext context;
    private JPanel contents;
    private JLabel locationTitle;
    private TextFieldWithBrowseButtonAndHint location;

    @SuppressWarnings("DialogTitleCapitalization")
    public InstallToolkitDialog(@Nullable Project project, @NotNull InstallToolkitDialogContext context) {

        super(project);
        this.context = context;

        this.init();
        this.setTitle(AutomateBundle.message("dialog.InstallToolkit.Title"));
        this.locationTitle.setText(AutomateBundle.message("dialog.InstallToolkit.Location.Title"));
        this.locationTitle.setLabelFor(this.location);
        this.location.setHint(AutomateBundle.message("dialog.InstallToolkit.LocationHint.Message", AutomateConstants.ToolkitFileExtension));
        this.location.addBrowseFolderListener(AutomateBundle.message("dialog.InstallToolkit.LocationPicker.Title", AutomateConstants.ToolkitFileExtension), null, project,
                                              FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor());
    }

    @TestOnly
    public static @Nullable ValidationInfo doValidate(InstallToolkitDialogContext ignoredContext, String location) {

        if (location.isEmpty()) {
            return new ValidationInfo(AutomateBundle.message("dialog.InstallToolkit.LocationValidation.None.Message", AutomateConstants.ToolkitFileExtension));
        }

        var file = new File(location);
        if (!file.isFile()) {
            return new ValidationInfo(AutomateBundle.message("dialog.InstallToolkit.LocationValidation.NotAFile.Message", AutomateConstants.ToolkitFileExtension));
        }

        return null;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {

        var location = this.location.getText();
        return doValidate(this.context, location);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {

        return this.contents;
    }

    @Override
    protected void doOKAction() {

        super.doOKAction();
        this.context.ToolkitLocation = this.location.getText();
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {

        return this.location;
    }

    public InstallToolkitDialogContext getContext() {

        return this.context;
    }

    public static class InstallToolkitDialogContext {

        public String ToolkitLocation;
    }
}
