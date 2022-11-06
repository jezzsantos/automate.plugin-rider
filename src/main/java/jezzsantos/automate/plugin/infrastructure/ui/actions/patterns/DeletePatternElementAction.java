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

public class DeletePatternElementAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public DeletePatternElementAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.DeletePatternElement.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isPatternEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isPatternEditingMode = application.getEditingMode() == EditingMode.PATTERNS;
        }

        var isElementSite = Selection.isChildElementAndNotRoot(e) != null;
        presentation.setEnabledAndVisible(isPatternEditingMode && isElementSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.pattern.element.delete", null);

        var project = e.getProject();
        if (project != null) {
            var selected = Selection.isChildElementAndNotRoot(e);
            if (selected != null) {
                if (ConfirmDeleteDialog.confirms(project,
                                                 AutomateBundle.message("dialog.ConfirmDelete.PatternElement.Title"),
                                                 AutomateBundle.message("dialog.ConfirmDelete.PatternElement.Message"))) {
                    var application = IAutomateApplication.getInstance(project);
                    Try.andHandle(project,
                                  () -> application.deletePatternElement(selected.getParent().getEditPath(), selected.getElement().getName(), selected.getElement().isCollection()),
                                  () -> this.onSuccess.run(model -> model.deleteElement(selected.getElement())),
                                  AutomateBundle.message("action.DeletePatternElement.DeleteElement.Failure.Message"));
                }
            }
        }
    }
}
