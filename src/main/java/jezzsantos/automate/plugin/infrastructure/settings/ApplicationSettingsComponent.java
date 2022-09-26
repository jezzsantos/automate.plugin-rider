package jezzsantos.automate.plugin.infrastructure.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.ui.DarculaColors;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UIUtil;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.services.interfaces.CliExecutableStatus;
import jezzsantos.automate.plugin.application.services.interfaces.IApplicationConfiguration;
import jezzsantos.automate.plugin.application.services.interfaces.IAutomateCliService;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.services.cli.IOsPlatform;
import jezzsantos.automate.plugin.infrastructure.ui.components.TextFieldWithBrowseButtonAndHint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class ApplicationSettingsComponent {

    private final JPanel minPanel;
    private final JBCheckBox authoringMode = new JBCheckBox(AutomateBundle.message("settings.AuthoringMode.Label.Message"));
    private final JBCheckBox viewCliLog = new JBCheckBox(AutomateBundle.message("settings.ViewCliLog.Label.Title"));
    private final TextFieldWithBrowseButtonAndHint pathToAutomateExecutable = new TextFieldWithBrowseButtonAndHint();
    private final JBLabel testPathToAutomateResult = new JBLabel();
    private final JBLabel helpLink = new JBLabel();
    private final String currentDirectory;

    public ApplicationSettingsComponent(@NotNull IOsPlatform platform) {

        var automateService = IAutomateCliService.getInstance();
        var defaultInstallLocation = automateService.getDefaultExecutableLocation();
        this.pathToAutomateExecutable.setHint(AutomateBundle.message("settings.PathToAutomateExecutable.EmptyPathHint.Message", defaultInstallLocation));
        this.pathToAutomateExecutable.setPreferredSize(new Dimension(380, this.pathToAutomateExecutable.getHeight()));
        this.pathToAutomateExecutable.addBrowseFolderListener(AutomateBundle.message("settings.PathToAutomateExecutable.Picker.Title"), null, null,
                                                              FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor());
        var testPathToAutomatePanel = new JPanel();
        testPathToAutomatePanel.setLayout(new BorderLayout());
        testPathToAutomatePanel.add(this.pathToAutomateExecutable, BorderLayout.LINE_START);
        var testPathToAutomate = new JButton(AutomateBundle.message("settings.TestPathToAutomateExecutable.Label.Title"));
        testPathToAutomatePanel.add(testPathToAutomate, BorderLayout.LINE_END);
        testPathToAutomate.addActionListener(e -> this.onTestPathToAutomate(e, automateService));
        initHelpLink(AutomateConstants.InstallationInstructionsUrl, AutomateBundle.message("settings.HelpLink.Title"));
        this.helpLink.setVisible(false);

        this.minPanel = FormBuilder.createFormBuilder()
          .addComponent(this.authoringMode, 1)
          .addLabeledComponent(new JBLabel(AutomateBundle.message("settings.PathToAutomateExecutable.Label.Title", AutomateConstants.ExecutableName)), testPathToAutomatePanel, 1,
                               false)
          .addComponentToRightColumn(this.testPathToAutomateResult)
          .addComponentToRightColumn(this.helpLink)
          .addComponent(this.viewCliLog, 1)
          .addComponentFillVertically(new JPanel(), 0)
          .getPanel();

        this.currentDirectory = platform.getDotNetInstallationDirectory();
        if (!automateService.isCliInstalled(this.currentDirectory)) {
            var configuration = IApplicationConfiguration.getInstance();
            var status = automateService.tryGetExecutableStatus(this.currentDirectory, configuration.getExecutablePath());
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

    private void onTestPathToAutomate(ActionEvent ignored, IAutomateCliService automateService) {

        var executablePath = this.pathToAutomateExecutable.getText();
        var executableStatus = automateService.tryGetExecutableStatus(this.currentDirectory, executablePath);

        displayVersionInfo(executableStatus);
    }

    private void displayVersionInfo(@NotNull CliExecutableStatus executableStatus) {

        var compatibility = executableStatus.getCompatibility();
        var executableName = executableStatus.getExecutableName();
        switch (compatibility) {
            case Supported -> {
                this.helpLink.setVisible(false);
                this.testPathToAutomateResult.setFontColor(UIUtil.FontColor.NORMAL);
                this.testPathToAutomateResult.setText(
                  AutomateBundle.message("settings.PathToAutomateExecutable.Supported.Message", executableName, executableStatus.getVersion()));
            }
            case UnSupported -> {
                this.helpLink.setVisible(true);
                this.testPathToAutomateResult.setForeground(DarculaColors.RED);
                this.testPathToAutomateResult.setText(
                  AutomateBundle.message("settings.PathToAutomateExecutable.Unsupported.Message", executableName, executableStatus.getMinCompatibleVersion()));
            }
            default -> {
                this.helpLink.setVisible(true);
                this.testPathToAutomateResult.setForeground(DarculaColors.RED);
                this.testPathToAutomateResult.setText(AutomateBundle.message("settings.PathToAutomateExecutable.Unknown.Message", executableName));
            }
        }
    }

    private void initHelpLink(final String url, String text) {

        this.helpLink.setText("<html><a href=\"\">" + text + "</a></html>");
        this.helpLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.helpLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception ignored) {
                }
            }
        });
    }
}
