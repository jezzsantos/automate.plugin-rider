package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElement;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.ConfirmDeleteDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.DraftElementPlaceholderNode;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.DraftTreeModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;
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

        var isPropertySite = getElement(e) != null;
        presentation.setEnabledAndVisible(isDraftEditingMode && isPropertySite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        var project = e.getProject();
        if (project != null) {
            var element = getElement(e);
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

    private DraftElement getElement(AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof DraftElementPlaceholderNode) {
                    var element = ((DraftElementPlaceholderNode) leaf).getElement();
                    if (element.isNotRoot()) {
                        return element;
                    }
                }
            }
        }

        return null;
    }
}
