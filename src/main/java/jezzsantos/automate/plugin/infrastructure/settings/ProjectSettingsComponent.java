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
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.components.TextFieldWithBrowseButtonAndHint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ProjectSettingsComponent {

    private final JPanel minPanel;
    private final JBCheckBox authoringMode = new JBCheckBox(AutomateBundle.message("settings.DeveloperMode.Label.Title"));
    private final TextFieldWithBrowseButtonAndHint pathToAutomateExecutable = new TextFieldWithBrowseButtonAndHint();
    private final JBLabel testPathToAutomateResult = new JBLabel();

    public ProjectSettingsComponent(Project project) {
        var application = IAutomateApplication.getInstance(project);
        var defaultInstallLocation = application.getDefaultInstallLocation();
        pathToAutomateExecutable.setHint(AutomateBundle.message("settings.PathToAutomateExecutable.EmptyPath.Title", defaultInstallLocation));
        pathToAutomateExecutable.setPreferredSize(new Dimension(380, pathToAutomateExecutable.getHeight()));
        pathToAutomateExecutable.addBrowseFolderListener(AutomateBundle.message("settings.PathToAutomateExecutable.Picker.Title"), null, project, FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor());
        var testPathToAutomatePanel = new JPanel();
        testPathToAutomatePanel.setLayout(new BorderLayout());
        testPathToAutomatePanel.add(pathToAutomateExecutable, BorderLayout.LINE_START);
        var testPathToAutomate = new JButton(AutomateBundle.message("settings.TestPathToAutomateExecutable.Label.Title"));
        testPathToAutomatePanel.add(testPathToAutomate, BorderLayout.LINE_END);
        testPathToAutomate.addActionListener(e -> this.onTestPathToAutomate(e, application));

        minPanel = FormBuilder.createFormBuilder()
                .addComponent(authoringMode, 1)
                .addLabeledComponent(new JBLabel(AutomateBundle.message("settings.PathToAutomateExecutable.Label.Title", AutomateConstants.ExecutableName)), testPathToAutomatePanel, 1, false)
                .addComponentToRightColumn(testPathToAutomateResult)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }


    public JPanel getPanel() {
        return minPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return authoringMode;
    }

    public boolean getAuthoringMode() {
        return authoringMode.isSelected();
    }

    public void setAuthoringMode(boolean value) {
        authoringMode.setSelected(value);
    }

    public String getPathToAutomateExecutable() {
        return pathToAutomateExecutable.getText();
    }

    public void setPathToAutomateExecutable(String value) {
        pathToAutomateExecutable.setText(value);
    }

    private void onTestPathToAutomate(ActionEvent e, IAutomateApplication automateApplication) {

        var executableName = automateApplication.getExecutableName();
        var version = automateApplication.tryGetExecutableVersion(pathToAutomateExecutable.getText());
        if (version == null) {
            testPathToAutomateResult.setForeground(DarculaColors.RED);
            testPathToAutomateResult.setText(AutomateBundle.message("settings.PathToAutomateExecutable.Invalid.Message", AutomateConstants.ExecutableName));
        } else {
            testPathToAutomateResult.setFontColor(UIUtil.FontColor.NORMAL);
            testPathToAutomateResult.setText(AutomateBundle.message("settings.PathToAutomateExecutable.Success.Message", AutomateConstants.ExecutableName, version));
        }

    }
}
