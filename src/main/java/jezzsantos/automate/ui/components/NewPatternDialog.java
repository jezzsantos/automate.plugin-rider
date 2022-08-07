package jezzsantos.automate.ui.components;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import jezzsantos.automate.AutomateBundle;
import jezzsantos.automate.AutomateConstants;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class NewPatternDialog extends DialogWrapper {

    public String Name;
    private JPanel contents;
    private JTextField name;

    protected NewPatternDialog(@Nullable Project project) {
        super(project);

        this.setTitle(AutomateBundle.message("dialog.NewPattern.Title"));
        this.init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contents;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return name;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        var text = name.getText();
        if (!text.matches(AutomateConstants.PatternNameRegex)) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewPattern.NameValidation"));
        }

        return null;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        Name = this.name.getText();
    }
}
