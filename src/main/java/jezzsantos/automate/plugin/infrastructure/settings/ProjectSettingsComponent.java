package jezzsantos.automate.plugin.infrastructure.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.ui.DarculaColors;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UIUtil;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.services.interfaces.CliExecutableStatus;
import jezzsantos.automate.plugin.application.services.interfaces.IConfiguration;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.components.TextFieldWithBrowseButtonAndHint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ProjectSettingsComponent {

    private final JPanel minPanel;
    private final JBCheckBox authoringMode = new JBCheckBox(AutomateBundle.message("settings.AuthoringMode.Label.Message"));
    private final JBCheckBox viewCliLog = new JBCheckBox(AutomateBundle.message("settings.ViewCliLog.Label.Title"));
    private final TextFieldWithBrowseButtonAndHint pathToAutomateExecutable = new TextFieldWithBrowseButtonAndHint();
    private final JBLabel testPathToAutomateResult = new JBLabel();

    public ProjectSettingsComponent(@NotNull Project project) {

        var application = IAutomateApplication.getInstance(project);
        var defaultInstallLocation = application.getDefaultExecutableLocation();
        this.pathToAutomateExecutable.setHint(AutomateBundle.message("settings.PathToAutomateExecutable.EmptyPathHint.Message", defaultInstallLocation));
        this.pathToAutomateExecutable.setPreferredSize(new Dimension(380, this.pathToAutomateExecutable.getHeight()));
        this.pathToAutomateExecutable.addBrowseFolderListener(AutomateBundle.message("settings.PathToAutomateExecutable.Picker.Title"), null, project,
                                                              FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor());
        var testPathToAutomatePanel = new JPanel();
        testPathToAutomatePanel.setLayout(new BorderLayout());
        testPathToAutomatePanel.add(this.pathToAutomateExecutable, BorderLayout.LINE_START);
        var testPathToAutomate = new JButton(AutomateBundle.message("settings.TestPathToAutomateExecutable.Label.Title"));
        testPathToAutomatePanel.add(testPathToAutomate, BorderLayout.LINE_END);
        testPathToAutomate.addActionListener(e -> this.onTestPathToAutomate(e, application));

        this.minPanel = FormBuilder.createFormBuilder()
          .addComponent(this.authoringMode, 1)
          .addLabeledComponent(new JBLabel(AutomateBundle.message("settings.PathToAutomateExecutable.Label.Title", AutomateConstants.ExecutableName)), testPathToAutomatePanel, 1,
                               false)
          .addComponentToRightColumn(this.testPathToAutomateResult)
          .addComponent(this.viewCliLog, 1)
          .addComponentFillVertically(new JPanel(), 0)
          .getPanel();

        if (!application.isCliInstalled()) {
            var configuration = IConfiguration.getInstance(project);
            var status = application.tryGetExecutableStatus(configuration.getExecutablePath());
            displayVersionInfo(status);
        }
    }

    public JPanel getPanel() {

        return this.minPanel;
    }

    public JComponent getPreferredFocusedComponent() {

        return this.authoringMode;
    }

    public boolean getAuthoringMode() {

        return this.authoringMode.isSelected();
    }

    public void setAuthoringMode(boolean value) {

        this.authoringMode.setSelected(value);
    }

    public boolean getViewCliLog() {

        return this.viewCliLog.isSelected();
    }

    public void setViewCliLog(boolean value) {

        this.viewCliLog.setSelected(value);
    }

    public String getPathToAutomateExecutable() {

        return this.pathToAutomateExecutable.getText();
    }

    public void setPathToAutomateExecutable(String value) {

        this.pathToAutomateExecutable.setText(value);
    }

    private void onTestPathToAutomate(ActionEvent ignored, IAutomateApplication automateApplication) {

        var executablePath = this.pathToAutomateExecutable.getText();
        var executableStatus = automateApplication.tryGetExecutableStatus(executablePath);

        displayVersionInfo(executableStatus);
    }

    private void displayVersionInfo(@NotNull CliExecutableStatus executableStatus) {

        var compatibility = executableStatus.getCompatibility();
        var executableName = executableStatus.getExecutableName();
        switch (compatibility) {
            case Supported:
                this.testPathToAutomateResult.setFontColor(UIUtil.FontColor.NORMAL);
                this.testPathToAutomateResult.setText(
                  AutomateBundle.message("settings.PathToAutomateExecutable.Supported.Message", executableName, executableStatus.getVersion()));
                break;
            case UnSupported:
                this.testPathToAutomateResult.setForeground(DarculaColors.RED);
                this.testPathToAutomateResult.setText(
                  AutomateBundle.message("settings.PathToAutomateExecutable.Unsupported.Message", executableName, executableStatus.getMinCompatibleVersion()));
                break;

            default:
                this.testPathToAutomateResult.setForeground(DarculaColors.RED);
                this.testPathToAutomateResult.setText(AutomateBundle.message("settings.PathToAutomateExecutable.Unknown.Message", executableName));
                break;
        }
    }
}
