package jezzsantos.automate.plugin.infrastructure.settings;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.ui.DarculaColors;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UIUtil;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.CliInstallPolicy;
import jezzsantos.automate.plugin.application.services.interfaces.CliExecutableStatus;
import jezzsantos.automate.plugin.application.services.interfaces.IApplicationConfiguration;
import jezzsantos.automate.plugin.application.services.interfaces.IAutomateCliService;
import jezzsantos.automate.plugin.common.StringWithImplicitDefault;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.services.cli.IOsPlatform;
import jezzsantos.automate.plugin.infrastructure.ui.components.TextFieldWithBrowseButtonAndHint;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static jezzsantos.automate.plugin.common.General.toHtmlLink;

public class ApplicationSettingsComponent {

    private final JPanel minPanel;
    private final JBCheckBox authoringMode = new JBCheckBox(AutomateBundle.message("settings.AuthoringMode.Label.Message"));
    private final JBCheckBox viewCliLog = new JBCheckBox(AutomateBundle.message("settings.ViewCliLog.Label.Title"));
    private final TextFieldWithBrowseButtonAndHint executablePath = new TextFieldWithBrowseButtonAndHint();
    private final JBLabel executablePathTestResult = new JBLabel();
    private final JBLabel helpLink = new JBLabel();
    private final JBCheckBox cliInstallPolicy = new JBCheckBox(AutomateBundle.message("settings.CliInstallPolicy.Label.Title"));
    private final String currentDirectory;

    public ApplicationSettingsComponent(@NotNull IOsPlatform platform) {

        var automateService = IAutomateCliService.getInstance();
        var defaultInstallLocation = automateService.getDefaultExecutableLocation();
        this.executablePath.setHint(AutomateBundle.message("settings.PathToAutomateExecutable.EmptyPathHint.Message", defaultInstallLocation));
        this.executablePath.setPreferredSize(new Dimension(380, this.executablePath.getHeight()));
        this.executablePath.addBrowseFolderListener(AutomateBundle.message("settings.PathToAutomateExecutable.Picker.Title"), null, null,
                                                    FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor());
        this.executablePath.getTextField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {setCliInstallPolicyEnabled();}

            @Override
            public void removeUpdate(DocumentEvent e) {setCliInstallPolicyEnabled();}

            @Override
            public void changedUpdate(DocumentEvent e) {setCliInstallPolicyEnabled();}
        });
        var testPathToAutomatePanel = new JPanel();
        testPathToAutomatePanel.setLayout(new BorderLayout());
        testPathToAutomatePanel.add(this.executablePath, BorderLayout.LINE_START);
        var testPathToAutomate = new JButton(AutomateBundle.message("settings.TestPathToAutomateExecutable.Label.Title"));
        testPathToAutomatePanel.add(testPathToAutomate, BorderLayout.LINE_END);
        testPathToAutomate.addActionListener(e -> this.onTestPathToAutomate(e, automateService));
        initHelpLink();
        this.helpLink.setVisible(false);

        this.minPanel = FormBuilder.createFormBuilder()
          .addComponent(this.authoringMode, 1)
          .addLabeledComponent(new JBLabel(AutomateBundle.message("settings.PathToAutomateExecutable.Label.Title", AutomateConstants.ExecutableName)), testPathToAutomatePanel, 1,
                               false)
          .addComponentToRightColumn(this.executablePathTestResult)
          .addComponentToRightColumn(this.helpLink)
          .addComponentToRightColumn(this.cliInstallPolicy)
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

    @NotNull
    public StringWithImplicitDefault getExecutablePath() {

        return ApplicationSettingsState.createExecutablePathWithValue(this.executablePath.getText());
    }

    public void setExecutablePath(StringWithImplicitDefault value) {

        this.executablePath.setText(value.getValue());
    }

    @NotNull
    public CliInstallPolicy getCliInstallPolicy() {

        return this.cliInstallPolicy.isSelected()
          ? CliInstallPolicy.AUTO_UPGRADE
          : CliInstallPolicy.NONE;
    }

    public void setCliInstallPolicy(CliInstallPolicy value) {

        this.cliInstallPolicy.setSelected(value == CliInstallPolicy.AUTO_UPGRADE);
    }

    private void setCliInstallPolicyEnabled() {

        var text = this.executablePath.getText();
        this.cliInstallPolicy.setEnabled(text.isEmpty());
    }

    private void onTestPathToAutomate(ActionEvent ignored, @NotNull IAutomateCliService automateService) {

        var testText = this.executablePath.getText();
        var executablePath = ApplicationSettingsState.createExecutablePathWithValue(testText);
        var executableStatus = automateService.tryGetExecutableStatus(this.currentDirectory, executablePath);

        displayVersionInfo(executableStatus);
    }

    private void displayVersionInfo(@NotNull CliExecutableStatus executableStatus) {

        var compatibility = executableStatus.getCompatibility();
        var executableName = executableStatus.getExecutableName();
        switch (compatibility) {
            case COMPATIBLE -> {
                this.helpLink.setVisible(false);
                this.executablePathTestResult.setFontColor(UIUtil.FontColor.NORMAL);
                this.executablePathTestResult.setText(
                  AutomateBundle.message("settings.PathToAutomateExecutable.Supported.Message", executableName, executableStatus.getVersion()));
            }
            case INCOMPATIBLE -> {
                this.helpLink.setVisible(true);
                this.executablePathTestResult.setForeground(DarculaColors.RED);
                this.executablePathTestResult.setText(
                  AutomateBundle.message("settings.PathToAutomateExecutable.Unsupported.Message", executableName, executableStatus.getMinCompatibleVersion()));
            }
            default -> {
                this.helpLink.setVisible(true);
                this.executablePathTestResult.setForeground(DarculaColors.RED);
                this.executablePathTestResult.setText(AutomateBundle.message("settings.PathToAutomateExecutable.Unknown.Message", executableName));
            }
        }
    }

    private void initHelpLink() {

        this.helpLink.setText(
          "<html>" + toHtmlLink(AutomateConstants.InstallationInstructionsUrl, AutomateBundle.message("general.ApplicationSettingsComponent.MoreInfoLink.Title")) + "</html>");
        this.helpLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.helpLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                try {
                    BrowserUtil.browse(AutomateConstants.InstallationInstructionsUrl);
                } catch (Exception ignored) {
                }
            }
        });
    }
}
