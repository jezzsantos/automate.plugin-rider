package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.NewPatternDialog;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class AddPatternAction extends AnAction {

    private final Consumer<PatternDefinition> onSelect;

    public AddPatternAction(Consumer<PatternDefinition> onSelect) {
        super();
        this.onSelect = onSelect;
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        var message = AutomateBundle.message("action.AddPattern.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setIcon(AllIcons.General.Add);

        boolean isAuthoringMode = false;
        boolean isPatternEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isAuthoringMode = application.isAuthoringMode();
            isPatternEditingMode = application.getEditingMode() == EditingMode.Patterns;
        }
        presentation.setEnabledAndVisible(isAuthoringMode && isPatternEditingMode);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            var patterns = application.getPatterns();
            var dialog = new NewPatternDialog(project, patterns);
            if (dialog.showAndGet()) {
                var name = dialog.Name;
                PatternDefinition pattern;
                try {
                    pattern = application.createPattern(name);
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to add new pattern", ex);
                }
                onSelect.accept(pattern);
            }
        }
    }
}
