package jezzsantos.automate.plugin.infrastructure.ui.actions.drafts;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElement;
import jezzsantos.automate.plugin.application.interfaces.patterns.Automation;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.drafts.ExecuteDraftLaunchPointDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.DraftTreeModel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ExecuteDraftLaunchPointAction extends AnAction {

    private final Action<DraftTreeModel> onSuccess;
    private final DraftElement parentElement;
    private final Automation automation;

    public ExecuteDraftLaunchPointAction(@NotNull DraftElement parentElement, @NotNull Automation automation, Action<DraftTreeModel> onSuccess) {

        super();
        this.parentElement = parentElement;
        this.automation = automation;
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        var message = AutomateBundle.message("action.ExecuteDraftLaunchPoint.Title", this.automation.getName());
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.draft.launchpoint.execute", null);

        var project = e.getProject();
        if (project != null) {

            var application = IAutomateApplication.getInstance(project);
            var context = new ExecuteDraftLaunchPointDialog.ExecuteDraftLaunchPointDialogContext(this.automation.getName(),
                                                                                                 () -> Try.andHandle(project, () -> application.executeLaunchPoint(
                                                                                                   Objects.requireNonNull(this.parentElement.getConfigurePath()),
                                                                                                   this.automation.getName()), AutomateBundle.message(
                                                                                                   "action.ExecuteDraftLaunchPoint.Failure.Message")));
            var dialog = new ExecuteDraftLaunchPointDialog(project, context);
            dialog.showAndGet();
            this.onSuccess.run(model -> {});
        }
    }
}
