package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;

public class NewPatternDialog extends DialogWrapper {

    private final NewPatternDialogContext context;
    private JPanel contents;
    private JTextField name;
    private JLabel nameTitle;

    public NewPatternDialog(@Nullable Project project, @NotNull NewPatternDialogContext context) {
        super(project);
        this.context = context;

        this.init();
        this.setTitle(AutomateBundle.message("dialog.NewPattern.Title"));
        nameTitle.setText(AutomateBundle.message("dialog.NewPattern.Name.Title"));
        name.setText(this.context.Name);
    }

    @TestOnly
    public static @Nullable ValidationInfo doValidate(NewPatternDialogContext context, String name) {
        if (!name.matches(AutomateConstants.PatternNameRegex)) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewPattern.NameValidation.NotMatch.Message"));
        }
        var patternExists = context.Patterns.stream()
                .anyMatch(pattern -> pattern.getName().equalsIgnoreCase(name));
        if (patternExists) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewPattern.NameValidation.Exists.Message"));
        }

        return null;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return name;
    }

    public NewPatternDialogContext getContext() {
        return this.context;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contents;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        var name = this.name.getText();
        return doValidate(this.context, name);
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        this.context.Name = this.name.getText();
    }
}
