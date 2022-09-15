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
        this.locationTitle.setText(AutomateBundle.message("dialog.InstallToolkit.Location.Title"));
        this.locationTitle.setLabelFor(this.location);
        this.location.setPreferredSize(new Dimension(380, this.location.getHeight()));
        this.location.addBrowseFolderListener(AutomateBundle.message("dialog.InstallToolkit.LocationPicker.Title"), null, project,
                                              FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {

        return this.contents;
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

        return this.location;
    }

    public InstallToolkitDialogContext getContext() {

        return this.context;
    }

    private void createUIComponents() {

        this.location = new TextFieldWithBrowseButtonAndHint();
        this.location.setHint(AutomateBundle.message("dialog.InstallToolkit.LocationHint.Message", AutomateConstants.ToolkitFileExtension));
    }

    public static class InstallToolkitDialogContext {

        public String ToolkitLocation;
    }
}
