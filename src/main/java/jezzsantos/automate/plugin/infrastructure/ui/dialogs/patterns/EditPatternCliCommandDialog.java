package jezzsantos.automate.plugin.infrastructure.ui.dialogs.patterns;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.patterns.Automation;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.AutomateColors;
import jezzsantos.automate.plugin.infrastructure.ui.components.HyperLink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.util.List;
import java.util.stream.Stream;

public class EditPatternCliCommandDialog extends DialogWrapper {

    private final EditPatternCliCommandDialogContext context;
    private JPanel contents;
    private JTextField name;
    private JTextField applicationName;
    private JTextArea arguments;
    private JLabel nameTitle;
    private JLabel applicationNameTitle;
    private JLabel argumentsTitle;
    private HyperLink argumentsDescription;

    public EditPatternCliCommandDialog(@NotNull Project project, @NotNull EditPatternCliCommandDialog.EditPatternCliCommandDialogContext context) {

        super(project);

        this.context = context;

        this.init();
        this.setTitle(context.getIsNew()
                        ? AutomateBundle.message("dialog.EditPatternCliCommand.NewCommand.Title")
                        : AutomateBundle.message("dialog.EditPatternCliCommand.UpdateCommand.Title"));
        this.nameTitle.setText(AutomateBundle.message("dialog.EditPatternCliCommand.Name.Title"));
        this.name.setText(this.context.getName());
        this.applicationNameTitle.setText(AutomateBundle.message("dialog.EditPatternCliCommand.ApplicationName.Title"));
        this.applicationName.setText(this.context.getApplicationName());
        this.applicationName.setFont(this.arguments.getFont());
        this.argumentsTitle.setText(AutomateBundle.message("dialog.EditPatternCliCommand.Arguments.Title"));
        this.arguments.setText(this.context.getArguments());
        this.argumentsDescription.setLink(AutomateBundle.message("dialog.EditPatternCliCommand.CommandTargetPathDescription.Message"),
                                          AutomateBundle.message("dialog.EditPatternCliCommand.CommandTargetPathDescription.Link"),
                                          AutomateConstants.TemplatingExpressionsUrl);
        this.argumentsDescription.setForeground(AutomateColors.getDisabledText());

        setOKButtonText(context.getIsNew()
                          ? AutomateBundle.message("dialog.EditPatternCliCommand.NewCommand.Confirm.Title")
                          : AutomateBundle.message("dialog.EditPatternCliCommand.UpdateCommand.Confirm.Title"));
        this.contents.invalidate();
    }

    @TestOnly
    public static @Nullable ValidationInfo doValidate(@NotNull EditPatternCliCommandDialog.EditPatternCliCommandDialogContext context,
                                                      @NotNull String name, @NotNull String applicationName) {

        if (!context.isValidName(name)) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternCliCommand.NameValidation.NotMatch.Message"));
        }
        if (!context.isAvailableName(name)) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternCliCommand.NameValidation.Exists.Message"));
        }
        if (!context.isValidApplicationName(applicationName)) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternCliCommand.ApplicationNameValidation.NotMatch.Message"));
        }

        return null;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {

        return doValidate(this.context, this.name.getText(), this.applicationName.getText());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {

        return this.contents;
    }

    @Override
    protected void doOKAction() {

        super.doOKAction();
        this.context.setName(this.name.getText());
        this.context.setApplicationName(this.applicationName.getText());
        this.context.setArguments(this.arguments.getText());
    }

    public EditPatternCliCommandDialog.EditPatternCliCommandDialogContext getContext() {

        return this.context;
    }

    public static class EditPatternCliCommandDialogContext {

        private final boolean isNew;
        private final List<Automation> automations;
        private final String originalName;
        private String name;
        private String applicationName;
        private String arguments;

        public EditPatternCliCommandDialogContext(@NotNull List<Automation> automations) {

            this.isNew = true;
            this.automations = automations;
            this.name = guessNextName(automations.stream().map(Automation::getName).toList());
            this.originalName = this.name;
            this.applicationName = "";
            this.arguments = "";
        }

        public EditPatternCliCommandDialogContext(@NotNull Automation automation, @NotNull List<Automation> automations) {

            this.isNew = false;
            this.automations = automations;
            this.name = automation.getName();
            this.originalName = automation.getName();
            this.applicationName = automation.getApplicationName();
            this.arguments = automation.getArguments();
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

        @NotNull
        public String getApplicationName() {

            return this.applicationName;
        }

        public void setApplicationName(String applicationName) {

            this.applicationName = applicationName;
        }

        public boolean isValidApplicationName(String applicationName) {

            return (applicationName != null && !applicationName.isEmpty());
        }

        @Nullable
        public String getArguments() {

            return this.arguments;
        }

        public void setArguments(String arguments) {

            this.arguments = arguments;
        }

        private String guessNextName(@NotNull List<String> names) {

            var counter = 1;
            var name = AutomateBundle.message("dialog.EditPatternCliCommand.CommandName.Format", names.size() + 1);
            while (names.stream().anyMatch(name::equalsIgnoreCase)) {
                counter += 1;
                name = AutomateBundle.message("dialog.EditPatternCliCommand.CommandName.Format", names.size() + counter);
            }

            return name;
        }
    }
}
