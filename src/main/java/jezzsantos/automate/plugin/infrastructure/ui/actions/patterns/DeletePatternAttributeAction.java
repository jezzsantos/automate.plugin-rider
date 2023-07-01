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

public class DeletePatternAttributeAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public DeletePatternAttributeAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.DeletePatternAttribute.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isPatternEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isPatternEditingMode = application.getEditingMode() == EditingMode.PATTERNS;
        }

        var isAttributeSite = Selection.isAttribute(e) != null;
        presentation.setEnabledAndVisible(isPatternEditingMode && isAttributeSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.pattern.attribute.delete", null);

        var project = e.getProject();
        if (project != null) {
            var selected = Selection.isAttribute(e);
            if (selected != null) {
                if (ConfirmDeleteDialog.confirms(project,
                                                 AutomateBundle.message("dialog.ConfirmDelete.PatternAttribute.Title"),
                                                 AutomateBundle.message("dialog.ConfirmDelete.PatternAttribute.Message"))) {
                    var application = IAutomateApplication.getInstance(project);
                    Try.andHandle(project, AutomateBundle.message("action.DeletePatternAttribute.DeleteAttribute.Progress.Title"),
                                  () -> application.deletePatternAttribute(selected.getParent().getEditPath(), selected.getAttribute().getName()),
                                  () -> this.onSuccess.run(model -> model.deleteAttribute(selected.getAttribute())),
                                  AutomateBundle.message("action.DeletePatternAttribute.DeleteAttribute.Failure.Message"));
                }
            }
        }
    }
}
