package jezzsantos.automate.plugin.infrastructure.ui.actions.patterns;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.services.interfaces.INotifier;
import jezzsantos.automate.plugin.application.services.interfaces.NotificationType;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.IContainer;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.patterns.PublishPatternDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternTreeModel;
import org.jetbrains.annotations.NotNull;

public class PublishPatternAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;
    private final INotifier notifier;

    public PublishPatternAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
        this.notifier = IContainer.getNotifier();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.PublishPattern.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isPatternEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isPatternEditingMode = application.getEditingMode() == EditingMode.PATTERNS;
        }

        var isRootSite = Selection.isPattern(e) != null;
        presentation.setEnabledAndVisible(isPatternEditingMode && isRootSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.pattern.publish", null);

        var root = Selection.isPattern(e);
        if (root != null) {
            var project = e.getProject();
            if (project != null) {
                var application = IAutomateApplication.getInstance(project);
                var pattern = Try.andHandle(project, application::getCurrentPatternDetailed,
                                            AutomateBundle.message("action.PublishPattern.GetCurrentPattern.Failure.Message"));
                if (pattern != null) {
                    var dialog = new PublishPatternDialog(project,
                                                          new PublishPatternDialog.PublishPatternDialogContext(pattern));
                    if (dialog.showAndGet()) {
                        var context = dialog.getContext();
                        var warning = Try.andHandle(project,
                                                    () -> application.publishCurrentPattern(context.getInstallLocally(), context.getCustomVersion()),
                                                    AutomateBundle.message("action.PublishPattern.Publish.Failure.Message"));
                        if (warning != null) {
                            this.notifier.alert(NotificationType.WARNING, AutomateBundle.message("action.PublishPattern.Publish.SuccessWithWarning.Title"), AutomateBundle.message(
                              "action.PublishPattern.Publish.SuccessWithWarning.Message", warning), null);
                        }
                        this.onSuccess.run(model -> {});
                    }
                }
            }
        }
    }
}
