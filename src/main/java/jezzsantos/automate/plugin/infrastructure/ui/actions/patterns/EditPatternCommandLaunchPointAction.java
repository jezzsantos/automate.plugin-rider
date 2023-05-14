package jezzsantos.automate.plugin.infrastructure.ui.actions.patterns;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.patterns.EditPatternCommandLaunchPointDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternTreeModel;
import org.jetbrains.annotations.NotNull;

public class EditPatternCommandLaunchPointAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public EditPatternCommandLaunchPointAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.EditPatternCommandLaunchPoint.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isPatternEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isPatternEditingMode = application.getEditingMode() == EditingMode.PATTERNS;
        }

        var isAutomationSite = Selection.isCommandLaunchPoint(e) != null;
        presentation.setEnabledAndVisible(isPatternEditingMode && isAutomationSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.pattern.commandlaunchpoint.edit", null);

        var project = e.getProject();
        if (project != null) {
            var selected = Selection.isCommandLaunchPoint(e);
            if (selected != null) {
                var application = IAutomateApplication.getInstance(project);
                var automations = selected.getParent().getAutomation();
                var dialog = new EditPatternCommandLaunchPointDialog(project,
                                                                     new EditPatternCommandLaunchPointDialog.EditPatternCommandLaunchPointDialogContext(selected.getAutomation(),
                                                                                                                                                        automations));
                if (dialog.showAndGet()) {
                    var context = dialog.getContext();
                    var command = Try.andHandle(project,
                                                () -> application.updatePatternCommandLaunchPoint(selected.getParent().getEditPath(), context.getId(), context.getName(),
                                                                                                  context.getAddIdentifiers(), context.getRemoveIdentifiers(), context.getFrom()),
                                                AutomateBundle.message("action.EditPatternCommandLaunchPoint.UpdateLaunchPoint.Failure.Message"));
                    if (command != null) {
                        this.onSuccess.run(model -> model.updateAutomation(command));
                    }
                }
            }
        }
    }
}
