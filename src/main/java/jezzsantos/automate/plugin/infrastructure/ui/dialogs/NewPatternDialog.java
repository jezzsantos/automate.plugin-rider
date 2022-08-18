package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class NewPatternDialog extends DialogWrapper {

    private final List<PatternDefinition> patterns;
    public String Name;
    private JPanel contents;
    private JTextField name;
    private JLabel nameTitle;

    public NewPatternDialog(@Nullable Project project, List<PatternDefinition> patterns) {
        super(project);
        this.patterns = patterns;

        this.init();
        this.setTitle(AutomateBundle.message("dialog.NewPattern.Title"));
        nameTitle.setText(AutomateBundle.message("dialog.NewPattern.Name.Title"));
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return name;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contents;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        var text = name.getText();
        if (!text.matches(AutomateConstants.PatternNameRegex)) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewPattern.NameValidation.NotMatch"));
        }
        var existing = this.patterns.stream().anyMatch(pattern -> pattern.getName().equalsIgnoreCase(text));
        if (existing) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewPattern.NameValidation.Exists"));
        }

        return null;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        Name = this.name.getText();
    }
}
