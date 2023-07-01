package jezzsantos.automate.plugin.infrastructure.ui.actions.drafts;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.ConfirmDeleteDialog;
import org.jetbrains.annotations.NotNull;

public class DeleteDraftAction extends AnAction {

    private final Runnable onSuccess;

    public DeleteDraftAction(Runnable onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.DeleteDraft.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isDraftEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isDraftEditingMode = application.getEditingMode() == EditingMode.DRAFTS;
        }

        var isRootSite = Selection.isRootElementOrIncompatibleDraft(e) != null;
        presentation.setEnabledAndVisible(isDraftEditingMode && isRootSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.draft.delete", null);

        var project = e.getProject();
        if (project != null) {
            var root = Selection.isRootElementOrIncompatibleDraft(e);
            if (root != null) {
                if (ConfirmDeleteDialog.confirms(project,
                                                 AutomateBundle.message("dialog.ConfirmDelete.Draft.Title"),
                                                 AutomateBundle.message("dialog.ConfirmDelete.Draft.Message"))) {
                    var application = IAutomateApplication.getInstance(project);
                    Try.andHandle(project, AutomateBundle.message("action.DeleteDraft.DeleteDraft.Progress.Title"),
                                  application::deleteCurrentDraft,
                                  AutomateBundle.message("action.DeleteDraft.DeleteDraft.Failure.Message"));
                    this.onSuccess.run();
                }
            }
        }
    }
}
