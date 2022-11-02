package jezzsantos.automate.plugin.infrastructure.ui.actions.drafts;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElement;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.ConfirmDeleteDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.DraftElementPlaceholderNode;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.DraftIncompatiblePlaceholderNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;
import java.util.Map;

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

        var isRootSite = getSelection(e) != null;
        presentation.setEnabledAndVisible(isDraftEditingMode && isRootSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.draft.delete", null);

        var project = e.getProject();
        if (project != null) {
            var root = getSelection(e);
            if (root != null) {
                if (ConfirmDeleteDialog.confirms(project,
                                                 AutomateBundle.message("dialog.ConfirmDelete.Draft.Title"),
                                                 AutomateBundle.message("dialog.ConfirmDelete.Draft.Message"))) {
                    var application = IAutomateApplication.getInstance(project);
                    Try.andHandle(project,
                                  application::deleteCurrentDraft,
                                  AutomateBundle.message("action.DeleteDraftElement.DeleteDraft.Failure.Message"));
                    this.onSuccess.run();
                }
            }
        }
    }

    private DraftElement getSelection(AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof DraftElementPlaceholderNode placeholder) {
                    var element = placeholder.getElement();
                    if (!element.isNotRoot()) {
                        return element;
                    }
                }
                if (leaf instanceof DraftIncompatiblePlaceholderNode placeholder) {
                    return new DraftElement(placeholder.getDraftName(), Map.of(), true);
                }
            }
        }

        return null;
    }
}
