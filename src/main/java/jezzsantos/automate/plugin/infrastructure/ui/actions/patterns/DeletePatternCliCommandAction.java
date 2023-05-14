package jezzsantos.automate.plugin.infrastructure.ui.actions.patterns;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.ConfirmDeleteDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternTreeModel;
import org.jetbrains.annotations.NotNull;

public class DeletePatternCliCommandAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public DeletePatternCliCommandAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.DeletePatternCliCommand.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isPatternEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isPatternEditingMode = application.getEditingMode() == EditingMode.PATTERNS;
        }

        var isAutomationSite = Selection.isCliCommand(e) != null;
        presentation.setEnabledAndVisible(isPatternEditingMode && isAutomationSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.pattern.clicommand.delete", null);

        var project = e.getProject();
        if (project != null) {
            var selected = Selection.isCliCommand(e);
            if (selected != null) {
                if (ConfirmDeleteDialog.confirms(project,
                                                 AutomateBundle.message("dialog.ConfirmDelete.DeletePatternCliCommand.Title"),
                                                 AutomateBundle.message("dialog.ConfirmDelete.DeletePatternCliCommand.Message"))) {
                    var application = IAutomateApplication.getInstance(project);
                    Try.andHandle(project,
                                  () -> application.deleteCommand(selected.getParent().getEditPath(), selected.getAutomation().getName()),
                                  () -> this.onSuccess.run(model -> model.deleteAutomation(selected.getAutomation())),
                                  AutomateBundle.message("action.DeletePatternCliCommand.DeleteCommand.Failure.Message"));
                }
            }
        }
    }
}
