package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.NewPatternDialog;
import org.jetbrains.annotations.NotNull;

public class AddPatternAction extends AnAction {
    private final Runnable onPerformed;

    public AddPatternAction(@NotNull Runnable onPerformed) {
        super();
        this.onPerformed = onPerformed;
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
                try {
                    application.createPattern(name);
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to add new pattern", ex);
                }
                onPerformed.run();
            }
        }
    }
}
