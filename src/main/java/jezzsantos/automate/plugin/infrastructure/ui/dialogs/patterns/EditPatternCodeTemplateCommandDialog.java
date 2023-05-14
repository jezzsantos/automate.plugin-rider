package jezzsantos.automate.plugin.infrastructure.ui.dialogs.patterns;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.patterns.Automation;
import jezzsantos.automate.plugin.application.interfaces.patterns.CodeTemplate;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.AutomateColors;
import jezzsantos.automate.plugin.infrastructure.ui.components.HyperLink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.util.List;
import java.util.stream.Stream;

public class EditPatternCodeTemplateCommandDialog extends DialogWrapper {

    private final EditPatternCodeTemplateCommandDialogContext context;
    private JPanel contents;
    private JLabel nameTitle;
    private JTextField name;
    private JComboBox<CodeTemplate> codeTemplates;
    private JTextField targetPath;
    private JCheckBox isOneOff;
    private JLabel codeTemplateNameTitle;
    private JLabel targetPathTitle;
    private HyperLink targetPathDescription;

    public EditPatternCodeTemplateCommandDialog(@NotNull Project project, @NotNull EditPatternCodeTemplateCommandDialogContext context) {

        super(project);

        this.context = context;

        this.init();
        this.setTitle(context.getIsNew()
                        ? AutomateBundle.message("dialog.EditPatternCodeTemplateCommand.NewCommand.Title")
                        : AutomateBundle.message("dialog.EditPatternCodeTemplateCommand.UpdateCommand.Title"));
        this.nameTitle.setText(AutomateBundle.message("dialog.EditPatternCodeTemplateCommand.Name.Title"));
        this.name.setText(this.context.getName());
        this.codeTemplateNameTitle.setText(AutomateBundle.message("dialog.EditPatternCodeTemplateCommand.CodeTemplateName.Title"));
        if (context.getIsNew()) {
            this.codeTemplates.addItem(null);
        }
        for (var template : this.context.getAvailableCodeTemplates()) {
            this.codeTemplates.addItem(template);
        }
        if (!context.getIsNew()) {
            if (this.context.getCodeTemplate() != null) {
                this.codeTemplates.setSelectedItem(this.context.getCodeTemplate());
            }
        }
        this.codeTemplates.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                this.context.setCodeTemplate(null);
            }
            if (e.getStateChange() == ItemEvent.SELECTED) {
                this.context.setCodeTemplate((CodeTemplate) e.getItem());
                initTargetPath();
            }
        });
        this.targetPathTitle.setText(AutomateBundle.message("dialog.EditPatternCodeTemplateCommand.TargetPath.Title"));
        this.targetPath.setText(this.context.getTargetPath());
        this.targetPathDescription.setLink(AutomateBundle.message("dialog.EditPatternCodeTemplate.CommandTargetPathDescription.Message"),
                                           AutomateBundle.message("dialog.EditPatternCodeTemplate.CommandTargetPathDescription.Link"),
                                           AutomateConstants.TemplatingExpressionsUrl);
        this.targetPathDescription.setForeground(AutomateColors.getDisabledText());
        this.isOneOff.setText(AutomateBundle.message("dialog.EditPatternCodeTemplateCommand.IsOneOff.Title"));
        this.isOneOff.setSelected(this.context.getIsOneOff());
        setOKButtonText(context.getIsNew()
                          ? AutomateBundle.message("dialog.EditPatternCodeTemplateCommand.NewCommand.Confirm.Title")
                          : AutomateBundle.message("dialog.EditPatternCodeTemplateCommand.UpdateCommand.Confirm.Title"));
        this.contents.invalidate();
    }

    @TestOnly
    public static @Nullable ValidationInfo doValidate(@NotNull EditPatternCodeTemplateCommandDialog.EditPatternCodeTemplateCommandDialogContext context,
                                                      @NotNull String name, @Nullable Object codeTemplate, @Nullable String targetPath) {

        if (!context.isValidName(name)) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternCodeTemplateCommand.NameValidation.NotMatch.Message"));
        }
        if (!context.isAvailableName(name)) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternCodeTemplateCommand.NameValidation.Exists.Message"));
        }
        if (codeTemplate == null) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternCodeTemplateCommand.CodeTemplateValidation.Exists.Message"));
        }
        if (!context.isValidTargetPath(targetPath)) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternCodeTemplateCommand.TargetPathValidation.NotMatch.Message"));
        }

        return null;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {

        return doValidate(this.context, this.name.getText(), this.codeTemplates.getSelectedItem(), this.targetPath.getText());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {

        return this.contents;
    }

    @Override
    protected void doOKAction() {

        super.doOKAction();
        this.context.setName(this.name.getText());
        this.context.setTargetPath(this.targetPath.getText());
        this.context.setIsOneOff(this.isOneOff.isSelected());
    }

    public EditPatternCodeTemplateCommandDialog.EditPatternCodeTemplateCommandDialogContext getContext() {

        return this.context;
    }

    private void initTargetPath() {

        var codeTemplate = (CodeTemplate) this.codeTemplates.getSelectedItem();
        if (codeTemplate == null) {
            return;
        }

        var file = new File(codeTemplate.getOriginalFilePath());
        if (!file.exists()) {
            return;
        }

        var existingText = this.targetPath.getText();
        if (!existingText.isEmpty()) {
            return;
        }

        var filename = String.format("~/%s", file.getName());
        this.targetPath.setText(filename);
    }

    public static class EditPatternCodeTemplateCommandDialogContext {

        private final boolean isNew;
        private final List<CodeTemplate> codeTemplates;
        private final List<Automation> automations;
        private final String originalName;
        private CodeTemplate codeTemplate;
        private String name;
        private String targetPath;
        private boolean isOneOff;

        public EditPatternCodeTemplateCommandDialogContext(@NotNull List<CodeTemplate> codeTemplates, @NotNull List<Automation> automations) {

            this.isNew = true;
            this.codeTemplates = codeTemplates;
            this.automations = automations;
            this.name = guessNextName(automations.stream().map(Automation::getName).toList());
            this.codeTemplate = null;
            this.originalName = this.name;
            this.targetPath = "";
            this.isOneOff = false;
        }

        public EditPatternCodeTemplateCommandDialogContext(@NotNull Automation automation, @NotNull List<CodeTemplate> codeTemplates, @NotNull List<Automation> automations) {

            this.isNew = false;
            this.codeTemplates = codeTemplates;
            this.automations = automations;
            this.name = automation.getName();
            this.originalName = automation.getName();
            this.codeTemplate = codeTemplates.stream().filter(ct -> ct.getId().equalsIgnoreCase(automation.getCodeTemplateId())).findFirst().orElse(null);
            this.targetPath = automation.getFilePath();
            this.isOneOff = automation.getIsOneOff();
        }

        public boolean getIsNew() {return this.isNew;}

        @NotNull
        public String getId() {

            return this.isNew
              ? this.name
              : this.originalName;
        }

        @NotNull
        public String getName() {

            return this.name;
        }

        public void setName(@NotNull String name) {

            this.name = name;
        }

        public boolean isAvailableName(@NotNull String name) {

            var existingNames = this.automations.stream()
              .map(Automation::getName);
            var reservedNames = AutomateConstants.ReservedAutomationNames.stream();
            var illegalNames = Stream.concat(existingNames, reservedNames);

            if (this.isNew) {
                return illegalNames
                  .noneMatch(in -> in.equalsIgnoreCase(name));
            }
            else {
                return illegalNames
                  .filter(in -> !in.equalsIgnoreCase(this.originalName))
                  .noneMatch(in -> in.equalsIgnoreCase(name));
            }
        }

        public boolean isValidName(@Nullable String name) {

            if (name == null || name.isEmpty()) {
                return false;
            }
            return name.matches(AutomateConstants.AutomationNameRegex);
        }

        public String getTargetPath() {

            return this.targetPath;
        }

        public void setTargetPath(String targetPath) {

            this.targetPath = targetPath;
        }

        public boolean isValidTargetPath(String targetPath) {

            return (targetPath != null && !targetPath.isEmpty());
        }

        public boolean getIsOneOff() {

            return this.isOneOff;
        }

        public void setIsOneOff(boolean selected) {

            this.isOneOff = selected;
        }

        public CodeTemplate getCodeTemplate() {

            return this.codeTemplate;
        }

        public void setCodeTemplate(CodeTemplate codeTemplate) {

            this.codeTemplate = codeTemplate;
        }

        @NotNull
        public List<CodeTemplate> getAvailableCodeTemplates() {

            return this.codeTemplates;
        }

        private String guessNextName(@NotNull List<String> names) {

            var counter = 1;
            var name = AutomateBundle.message("dialog.EditPatternCodeTemplate.CommandName.Format", names.size() + 1);
            while (names.stream().anyMatch(name::equalsIgnoreCase)) {
                counter += 1;
                name = AutomateBundle.message("dialog.EditPatternCodeTemplate.CommandName.Format", names.size() + counter);
            }

            return name;
        }
    }
}
