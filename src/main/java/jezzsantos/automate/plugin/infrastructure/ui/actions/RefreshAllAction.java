package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import org.jetbrains.annotations.NotNull;

public class RefreshAllAction extends AnAction {

    private final Runnable onPerformed;

    public RefreshAllAction(Runnable onPerformed) {

        super();
        this.onPerformed = onPerformed;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {

        return ActionUpdateThread.EDT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.RefreshPatterns.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setIcon(AllIcons.Actions.Refresh);
        presentation.setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.refresh", null);

        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            Try.andHandle(project, AutomateBundle.message("action.RefreshPatterns.ListAllItems.Progress.Title"),
                          () -> application.listAllAutomation(true),
                          this.onPerformed,
                          AutomateBundle.message("action.RefreshPatterns.ListAllItems.Failure.Message"));
        }
    }
}
