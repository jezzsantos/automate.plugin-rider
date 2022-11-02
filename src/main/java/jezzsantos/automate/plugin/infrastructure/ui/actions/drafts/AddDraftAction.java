package jezzsantos.automate.plugin.infrastructure.ui.actions.drafts;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.drafts.NewDraftDialog;
import org.jetbrains.annotations.NotNull;

public class AddDraftAction extends AnAction {

    private final Runnable onPerformed;

    public AddDraftAction(@NotNull Runnable onPerformed) {

        super();
        this.onPerformed = onPerformed;
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.AddDraft.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setIcon(AllIcons.General.Add);

        boolean isInstalled = false;
        boolean isDraftEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isInstalled = application.isCliInstalled();
            isDraftEditingMode = application.getEditingMode() == EditingMode.DRAFTS;
        }
        presentation.setEnabledAndVisible(isInstalled && isDraftEditingMode);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.draft.add", null);

        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            var drafts = application.listDrafts();
            var toolkits = application.listToolkits();
            var dialog = new NewDraftDialog(project, new NewDraftDialog.NewDraftDialogContext(toolkits, drafts));
            if (dialog.showAndGet()) {
                var context = dialog.getContext();
                var draft = Try.andHandle(project,
                                          () -> application.createDraft(context.ToolkitName, context.Name),
                                          AutomateBundle.message("action.AddDraft.NewDraft.Failure.Message"));
                if (draft != null) {
                    this.onPerformed.run();
                }
            }
        }
    }
}
