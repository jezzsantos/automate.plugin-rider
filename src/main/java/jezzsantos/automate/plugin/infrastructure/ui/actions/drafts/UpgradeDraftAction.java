package jezzsantos.automate.plugin.infrastructure.ui.actions.drafts;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.drafts.UpgradeDraftDialog;
import org.jetbrains.annotations.NotNull;

public class UpgradeDraftAction extends AnAction {

    private final Runnable onPerformed;

    public UpgradeDraftAction(@NotNull Runnable onPerformed) {

        super();
        this.onPerformed = onPerformed;
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.UpgradeDraft.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setIcon(AllIcons.Ide.Notification.PluginUpdate);

        boolean isDraftEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isDraftEditingMode = application.getEditingMode() == EditingMode.DRAFTS;
        }

        var isIncompatible = Selection.isIncompatibleDraft(e);
        var incompatibleSite = isIncompatible != null && (isIncompatible.isDraftIncompatible());
        presentation.setEnabledAndVisible(isDraftEditingMode && incompatibleSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.draft.upgrade", null);

        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            var selectedNode = Selection.isIncompatibleDraft(e);
            if (selectedNode != null) {
                var dialog = new UpgradeDraftDialog(project,
                                                    new UpgradeDraftDialog.UpgradeDraftDialogContext(selectedNode.getToolkitName(), selectedNode.getDraftCompatibility(),
                                                                                                     context -> Try.andHandle(project, AutomateBundle.message(
                                                                                                                                "action.UpgradeDraft.UpgradeCurrent.Progress.Title"),
                                                                                                                              () -> application.upgradeCurrentDraft(
                                                                                                                                context.getForce()),
                                                                                                                              AutomateBundle.message(
                                                                                                                                "action.UpgradeDraft.UpgradeCurrent.Failure.Message"))));
                if (dialog.showAndGet()) {
                    var context = dialog.getContext();
                    if (context.isSuccess()) {
                        this.onPerformed.run();
                    }
                }
            }
        }
    }
}
