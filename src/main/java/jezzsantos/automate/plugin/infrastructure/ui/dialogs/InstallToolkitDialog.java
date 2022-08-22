package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.components.TextFieldWithBrowseButtonAndHint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class InstallToolkitDialog extends DialogWrapper {

    private final InstallToolkitDialogContext context;
    private JPanel contents;
    private JLabel locationTitle;
    private TextFieldWithBrowseButtonAndHint location;

    public InstallToolkitDialog(@Nullable Project project, @NotNull InstallToolkitDialogContext context) {
        super(project);
        this.context = context;

        this.init();
        this.setTitle(AutomateBundle.message("dialog.InstallToolkit.Title"));
        locationTitle.setText(AutomateBundle.message("dialog.InstallToolkit.Location.Title"));
        locationTitle.setLabelFor(this.location);
        location.setPreferredSize(new Dimension(380, location.getHeight()));
        location.addBrowseFolderListener(AutomateBundle.message("dialog.InstallToolkit.LocationPicker.Title"), null, project, FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor());
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
    public @Nullable JComponent getPreferredFocusedComponent() {
        return contents;
    }

    public InstallToolkitDialogContext getContext() {
        return this.context;
    }

    private void createUIComponents() {
        this.location = new TextFieldWithBrowseButtonAndHint();
        this.location.setHint(AutomateBundle.message("dialog.InstallToolkit.LocationHint.Message", AutomateConstants.ToolkitFileExtension));
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contents;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        var location = this.location.getText();
        return doValidate(this.context, location);
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        this.context.ToolkitLocation = this.location.getText();
    }
}
