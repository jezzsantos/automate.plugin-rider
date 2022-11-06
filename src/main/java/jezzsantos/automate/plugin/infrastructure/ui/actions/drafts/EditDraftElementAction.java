package jezzsantos.automate.plugin.infrastructure.ui.actions.drafts;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.drafts.EditDraftElementDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.DraftTreeModel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EditDraftElementAction extends AnAction {

    private final Action<DraftTreeModel> onSuccess;

    public EditDraftElementAction(Action<DraftTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        var message = AutomateBundle.message("action.EditDraftElement.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isDraftEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isDraftEditingMode = application.getEditingMode() == EditingMode.DRAFTS;
        }

        var isElementSite = Selection.isChildElementOrRoot(e) != null;
        presentation.setEnabledAndVisible(isDraftEditingMode && isElementSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.draft.element.configure", null);

        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            var selectedNode = Selection.isChildElementOrRoot(e);
            if (selectedNode != null) {
                var element = selectedNode.getElement();
                var schema = Objects.requireNonNull(selectedNode.getSchema()).getSchema();
                var dialog = new EditDraftElementDialog(project, new EditDraftElementDialog.EditDraftElementDialogContext(element, schema));
                if (dialog.showAndGet()) {
                    var context = dialog.getContext();
                    var updated = Try.andHandle(project,
                                                () -> application.updateDraftElement(Objects.requireNonNull(element.getConfigurePath()), context.getValues()),
                                                AutomateBundle.message("action.EditDraftElement.UpdateElement.Failure.Message"));
                    if (updated != null) {
                        this.onSuccess.run(model -> model.updateDraftElement(updated));
                    }
                }
            }
        }
    }
}
