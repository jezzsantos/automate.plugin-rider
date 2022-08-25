package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.ExceptionHandler;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.NewPatternDialog;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.NewPatternDialogContext;
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
            var patterns = application.listPatterns();
            var dialog = new NewPatternDialog(project, new NewPatternDialogContext(patterns));
            if (dialog.showAndGet()) {
                var context = dialog.getContext();
                try {
                    application.createPattern(context.Name);
                } catch (Exception ex) {
                    ExceptionHandler.handle(project, ex, AutomateBundle.message("action.AppPattern.FailureNotification.Title"));
                }
                this.onPerformed.run();
            }
        }
    }
}
