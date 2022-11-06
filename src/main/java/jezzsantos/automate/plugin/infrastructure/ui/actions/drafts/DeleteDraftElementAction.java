package jezzsantos.automate.plugin.infrastructure.ui.actions.drafts;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.ConfirmDeleteDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.DraftTreeModel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DeleteDraftElementAction extends AnAction {

    private final Action<DraftTreeModel> onSuccess;

    public DeleteDraftElementAction(Action<DraftTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.DeleteDraftElement.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isDraftEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isDraftEditingMode = application.getEditingMode() == EditingMode.DRAFTS;
        }

        var isElementSite = Selection.isChildElementAndNotRoot(e) != null;
        presentation.setEnabledAndVisible(isDraftEditingMode && isElementSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.draft.element.delete", null);

        var project = e.getProject();
        if (project != null) {
            var element = Selection.isChildElementAndNotRoot(e);
            if (element != null) {
                if (ConfirmDeleteDialog.confirms(project,
                                                 AutomateBundle.message("dialog.ConfirmDelete.DraftElement.Title"),
                                                 AutomateBundle.message("dialog.ConfirmDelete.DraftElement.Message"))) {
                    var application = IAutomateApplication.getInstance(project);
                    Try.andHandle(project,
                                  () -> application.deleteDraftElement(Objects.requireNonNull(element.getConfigurePath())),
                                  () -> this.onSuccess.run(model -> model.deleteDraftElement(element)),
                                  AutomateBundle.message("action.DeleteDraftElement.DeleteElement.Failure.Message"));
                }
            }
        }
    }
}
