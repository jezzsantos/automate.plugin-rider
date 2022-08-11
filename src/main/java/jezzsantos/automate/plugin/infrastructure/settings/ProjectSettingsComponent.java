package jezzsantos.automate.plugin.infrastructure.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.ui.DarculaColors;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UIUtil;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.components.TextFieldWithBrowseButtonAndHint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ProjectSettingsComponent {

    private final JPanel minPanel;
    private final JBCheckBox developerMode = new JBCheckBox(AutomateBundle.message("settings.DeveloperMode.Label.Title"));
    private final TextFieldWithBrowseButtonAndHint pathToAutomateExecutable = new TextFieldWithBrowseButtonAndHint();
    private final JBLabel testPathToAutomateResult = new JBLabel();

    public ProjectSettingsComponent(Project project, IAutomateApplication automateApplication) {
        pathToAutomateExecutable.setPreferredSize(new Dimension(380, pathToAutomateExecutable.getHeight()));
        var testPathToAutomatePanel = new JPanel();
        testPathToAutomatePanel.setLayout(new BorderLayout());
        testPathToAutomatePanel.add(pathToAutomateExecutable, BorderLayout.LINE_START);
        var testPathToAutomate = new JButton(AutomateBundle.message("settings.TestPathToAutomateExecutable.Label.Title"));
        testPathToAutomatePanel.add(testPathToAutomate, BorderLayout.LINE_END);
        var defaultInstallLocation = automateApplication.getDefaultInstallLocation();
        pathToAutomateExecutable.setHint(String.format("Auto-detected: %s", defaultInstallLocation));
        pathToAutomateExecutable.addBrowseFolderListener(AutomateBundle.message("settings.PathToAutomateExecutable.Picker.Title"), null, project, FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor());
        testPathToAutomate.addActionListener(e -> this.onTestPathToAutomate(e, automateApplication));

        minPanel = FormBuilder.createFormBuilder()
                .addComponent(developerMode, 1)
                .addLabeledComponent(new JBLabel(AutomateBundle.message("settings.PathToAutomateExecutable.Label.Title")), testPathToAutomatePanel, 1, false)
                .addComponentToRightColumn(testPathToAutomateResult)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }


    public JPanel getPanel() {
        return minPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return developerMode;
    }

    public boolean getDeveloperMode() {
        return developerMode.isSelected();
    }

    public void setDeveloperMode(boolean value) {
        developerMode.setSelected(value);
    }

    public String getPathToAutomateExecutable() {
        return pathToAutomateExecutable.getText();
    }

    public void setPathToAutomateExecutable(String value) {
        pathToAutomateExecutable.setText(value);
    }

    private void onTestPathToAutomate(ActionEvent ignoredE, IAutomateApplication automateApplication) {

        var executableName = automateApplication.getExecutableName();
        var version = automateApplication.tryGetExecutableVersion(pathToAutomateExecutable.getText());
        if (version == null) {
            testPathToAutomateResult.setForeground(DarculaColors.RED);
            testPathToAutomateResult.setText(String.format("%s is not installed on this machine!", executableName));
        } else {
            testPathToAutomateResult.setFontColor(UIUtil.FontColor.NORMAL);
            testPathToAutomateResult.setText(String.format("%s version is %s", executableName, version));
        }

    }
}
