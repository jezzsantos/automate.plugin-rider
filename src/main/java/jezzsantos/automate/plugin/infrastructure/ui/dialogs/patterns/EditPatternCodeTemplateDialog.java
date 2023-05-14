package jezzsantos.automate.plugin.infrastructure.ui.dialogs.patterns;

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.patterns.Automation;
import jezzsantos.automate.plugin.application.interfaces.patterns.CodeTemplate;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.AutomateColors;
import jezzsantos.automate.plugin.infrastructure.ui.components.HyperLink;
import jezzsantos.automate.plugin.infrastructure.ui.components.TextFieldWithBrowseButtonAndHint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.File;
import java.util.List;
import java.util.stream.Stream;

public class EditPatternCodeTemplateDialog extends DialogWrapper {

    private final EditPatternCodeTemplateDialogContext context;
    private JPanel contents;
    private JTextField name;
    private JLabel filePathTitle;
    private TextFieldWithBrowseButtonAndHint filePath;
    private JLabel nameTitle;
    private JCheckBox addCommand;
    private JTextField commandName;
    private JLabel commandNameTitle;
    private JTextField commandTargetPath;
    private JLabel commandTargetPathTitle;
    private JCheckBox commandIsOneOff;
    private HyperLink commandTargetPathDescription;

    public EditPatternCodeTemplateDialog(@NotNull Project project, @NotNull EditPatternCodeTemplateDialog.EditPatternCodeTemplateDialogContext context) {

        super(project);

        this.context = context;

        this.init();
        this.setTitle(AutomateBundle.message("dialog.EditPatternCodeTemplate.NewTemplate.Title"));
        this.nameTitle.setText(AutomateBundle.message("dialog.EditPatternCodeTemplate.Name.Title"));
        this.name.setText(this.context.getName());
        this.filePathTitle.setText(AutomateBundle.message("dialog.EditPatternCodeTemplate.FilePath.Title"));
        this.filePath.setHint(AutomateBundle.message("dialog.EditPatternCodeTemplate.FilePathHint.Message"));
        this.filePath.addBrowseFolderListener(AutomateBundle.message("dialog.EditPatternCodeTemplate.FilePathPicker.Title"), null, project,
                                              FileChooserDescriptorFactory.createSingleFileOrExecutableAppDescriptor());
        this.filePath.getTextField().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {initCommandTargetPath();}

            @Override
            public void removeUpdate(DocumentEvent e) {initCommandTargetPath();}

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        this.addCommand.setText(AutomateBundle.message("dialog.EditPatternCodeTemplate.AddCommand.Title"));
        initAddCommand(this.context.isAddCommand());
        this.addCommand.addActionListener(e -> {
            var isAddCommand = this.addCommand.isSelected();
            initAddCommand(isAddCommand);
        });
        this.commandNameTitle.setText(AutomateBundle.message("dialog.EditPatternCodeTemplate.CommandName.Title"));
        this.commandName.setText(this.context.getCommandName());
        this.commandTargetPathTitle.setText(AutomateBundle.message("dialog.EditPatternCodeTemplate.CommandTargetPath.Title"));
        this.commandTargetPath.setText(this.context.getCommandTargetPath());
        this.commandTargetPathDescription.setLink(AutomateBundle.message("dialog.EditPatternCodeTemplate.CommandTargetPathDescription.Message"),
                                                  AutomateBundle.message("dialog.EditPatternCodeTemplate.CommandTargetPathDescription.Link"),
                                                  AutomateConstants.TemplatingExpressionsUrl);
        this.commandTargetPathDescription.setForeground(AutomateColors.getDisabledText());
        this.commandIsOneOff.setText(AutomateBundle.message("dialog.EditPatternCodeTemplate.CommandIsOneOff.Title"));
        this.commandIsOneOff.setSelected(this.context.getCommandIsOneOff());
        setOKButtonText(AutomateBundle.message("dialog.EditPatternCodeTemplate.NewCodeTemplate.Confirm.Title"));
        this.contents.invalidate();
    }

    @TestOnly
    public static @Nullable ValidationInfo doValidate(@NotNull EditPatternCodeTemplateDialog.EditPatternCodeTemplateDialogContext context,
                                                      @NotNull String name, @Nullable String filePath,
                                                      @NotNull String commandName, @Nullable String commandTargetPath) {

        if (!context.isValidName(name)) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternCodeTemplate.NameValidation.NotMatch.Message"));
        }
        if (!context.isAvailableName(name)) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternCodeTemplate.NameValidation.Exists.Message"));
        }
        if (!context.isValidFilePath(filePath)) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternCodeTemplate.FilePathValidation.NotMatch.Message"));
        }
        if (context.isAddCommand()) {
            if (!commandName.isEmpty()) {
                if (!context.isValidCommandName(commandName)) {
                    return new ValidationInfo(AutomateBundle.message("dialog.EditPatternCodeTemplate.NameValidation.NotMatch.Message"));
                }
                if (!context.isAvailableCommandName(name)) {
                    return new ValidationInfo(AutomateBundle.message("dialog.EditPatternCodeTemplate.CommandNameValidation.Exists.Message"));
                }
            }
            if (!context.isValidCommandTargetPath(commandTargetPath)) {
                return new ValidationInfo(AutomateBundle.message("dialog.EditPatternCodeTemplate.TargetPathValidation.NotMatch.Message"));
            }
        }

        return null;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {

        return doValidate(this.context, this.name.getText(), this.filePath.getText(), this.commandName.getText(), this.commandTargetPath.getText());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {

        return this.contents;
    }

    @Override
    protected void doOKAction() {

        super.doOKAction();
        this.context.setName(this.name.getText());
        this.context.setFilePath(this.filePath.getText());
        this.context.setAddCommand(this.addCommand.isSelected());
        this.context.setCommandName(this.commandName.getText());
        this.context.setCommandTargetPath(this.commandTargetPath.getText());
        this.context.setCommandIsOneOff(this.commandIsOneOff.isSelected());
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {

        return this.name;
    }

    public EditPatternCodeTemplateDialog.EditPatternCodeTemplateDialogContext getContext() {

        return this.context;
    }

    private void initAddCommand(boolean isAddCommand) {

        this.context.setAddCommand(isAddCommand);
        this.addCommand.setSelected(isAddCommand);
        this.commandName.setEnabled(isAddCommand);
        this.commandTargetPath.setEnabled(isAddCommand);
        this.commandIsOneOff.setEnabled(isAddCommand);
    }

    private void initCommandTargetPath() {

        var filePath = this.filePath.getText();
        if (filePath.isEmpty()) {
            return;
        }

        var file = new File(filePath);
        if (!file.exists()) {
            return;
        }

        var existingText = this.commandTargetPath.getText();
        if (!existingText.isEmpty()) {
            return;
        }

        var filename = String.format("~/%s", file.getName());
        this.commandTargetPath.setText(filename);
    }

    public static class EditPatternCodeTemplateDialogContext {

        private final List<CodeTemplate> codeTemplates;
        private final List<Automation> automations;
        private String commandName;
        private String commandTargetPath;
        private boolean commandIsOneOff;
        private boolean isAddCommand;
        private String name;
        private String filePath;

        public EditPatternCodeTemplateDialogContext(@NotNull List<CodeTemplate> codeTemplates, @NotNull List<Automation> automations) {

            this.codeTemplates = codeTemplates;
            this.automations = automations;
            this.name = guessNextName("dialog.EditPatternCodeTemplate.Name.Format", codeTemplates.stream().map(CodeTemplate::getName).toList());
            this.isAddCommand = true;
            this.commandName = guessNextName("dialog.EditPatternCodeTemplate.CommandName.Format", automations.stream().map(Automation::getName).toList());
            this.commandTargetPath = "";
            this.commandIsOneOff = false;
        }

        @NotNull
        public String getName() {

            return this.name;
        }

        public void setName(@NotNull String name) {

            this.name = name;
        }

        public boolean isAvailableName(@NotNull String name) {

            var existingNames = this.codeTemplates.stream()
              .map(CodeTemplate::getName);
            var reservedNames = AutomateConstants.ReservedCodeTemplateNames.stream();
            var illegalNames = Stream.concat(existingNames, reservedNames);

            return illegalNames
              .noneMatch(in -> in.equalsIgnoreCase(name));
        }

        public boolean isValidName(@Nullable String name) {

            if (name == null || name.isEmpty()) {
                return false;
            }
            return name.matches(AutomateConstants.CodeTemplateNameRegex);
        }

        @NotNull
        public String getFilePath() {

            return this.filePath;
        }

        public void setFilePath(@NotNull String filePath) {

            this.filePath = filePath;
        }

        public boolean isValidFilePath(String filePath) {

            return new File(filePath).exists();
        }

        public String getCommandName() {

            return this.commandName;
        }

        public void setCommandName(@NotNull String name) {

            this.commandName = name;
        }

        public boolean isAvailableCommandName(@NotNull String name) {

            var existingNames = this.automations.stream()
              .map(Automation::getName);
            var reservedNames = AutomateConstants.ReservedAutomationNames.stream();
            var illegalNames = Stream.concat(existingNames, reservedNames);

            return illegalNames
              .noneMatch(in -> in.equalsIgnoreCase(name));
        }

        public boolean isValidCommandName(@Nullable String commandName) {

            if (commandName == null || commandName.isEmpty()) {
                return false;
            }
            return commandName.matches(AutomateConstants.AutomationNameRegex);
        }

        public String getCommandTargetPath() {

            return this.commandTargetPath;
        }

        public void setCommandTargetPath(String targetPath) {

            this.commandTargetPath = targetPath;
        }

        public boolean isValidCommandTargetPath(String targetPath) {

            return (targetPath != null && !targetPath.isEmpty());
        }

        public boolean getCommandIsOneOff() {

            return this.commandIsOneOff;
        }

        public void setCommandIsOneOff(boolean selected) {

            this.commandIsOneOff = selected;
        }

        public boolean isAddCommand() {

            return this.isAddCommand;
        }

        public void setAddCommand(boolean selected) {

            this.isAddCommand = selected;
        }

        private String guessNextName(@NotNull String format, @NotNull List<String> names) {

            var counter = 1;
            var name = AutomateBundle.message(format, names.size() + 1);
            while (names.stream().anyMatch(name::equalsIgnoreCase)) {
                counter += 1;
                name = AutomateBundle.message(format, names.size() + counter);
            }

            return name;
        }
    }
}
