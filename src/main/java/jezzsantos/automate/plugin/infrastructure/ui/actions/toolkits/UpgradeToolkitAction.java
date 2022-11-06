package jezzsantos.automate.plugin.infrastructure.ui.actions.toolkits;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.services.interfaces.INotifier;
import jezzsantos.automate.plugin.application.services.interfaces.NotificationType;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.IContainer;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.actions.drafts.Selection;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.toolkits.UpgradeToolkitDialog;
import org.jetbrains.annotations.NotNull;

public class UpgradeToolkitAction extends AnAction {

    private final Runnable onPerformed;
    private final INotifier notifier;

    public UpgradeToolkitAction(@NotNull Runnable onPerformed) {

        super();
        this.onPerformed = onPerformed;
        this.notifier = IContainer.getNotifier();
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.UpgradeToolkit.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setIcon(AllIcons.Ide.Notification.PluginUpdate);

        boolean isDraftEditingMode = false;
        var isIncompatible = Selection.isIncompatibleDraft(e);
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isDraftEditingMode = application.getEditingMode() == EditingMode.DRAFTS;
        }

        var incompatibleSite = isIncompatible != null && (isIncompatible.isRuntimeIncompatible());
        presentation.setEnabledAndVisible(isDraftEditingMode && incompatibleSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.toolkit.upgrade", null);

        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            var selectedNode = Selection.isIncompatibleDraft(e);
            if (selectedNode != null) {
                var patternId = selectedNode.getToolkitId();
                var toolkitIsUpgradeable = application.findToolkitById(patternId) != null;
                var dialog = new UpgradeToolkitDialog(project,
                                                      new UpgradeToolkitDialog.UpgradeToolkitDialogContext(selectedNode.getToolkitName(), selectedNode.getToolkitCompatibility(),
                                                                                                           toolkitIsUpgradeable));
                if (dialog.showAndGet()) {
                    var warning = Try.andHandle(project, () -> {
                                                    application.setCurrentPattern(patternId);
                                                    return application.publishCurrentPattern(true, null);
                                                },
                                                AutomateBundle.message(
                                                  "action.UpgradeToolkit.Failure.Message"));
                    if (warning != null) {
                        this.notifier.alert(NotificationType.WARNING, AutomateBundle.message("action.UpgradeToolkit.Publish.SuccessWithWarning.Title"), AutomateBundle.message(
                          "action.UpgradeToolkit.Publish.SuccessWithWarning.Message", warning), null);
                    }
                    this.onPerformed.run();
                }
            }
        }
    }
}
