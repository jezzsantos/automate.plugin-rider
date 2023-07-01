package jezzsantos.automate.plugin.infrastructure.ui.actions.toolkits;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.toolkits.InstallToolkitDialog;
import org.jetbrains.annotations.NotNull;

public class InstallToolkitToolbarAction extends AnAction {

    private final Runnable onPerformed;

    public InstallToolkitToolbarAction(@NotNull Runnable onPerformed) {

        super();
        this.onPerformed = onPerformed;
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.InstallToolkit.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setIcon(AllIcons.Actions.Install);

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

        IRecorder.getInstance().measureEvent("action.toolkit.install", null);

        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            var dialog = new InstallToolkitDialog(project, new InstallToolkitDialog.InstallToolkitDialogContext());
            if (dialog.showAndGet()) {
                var context = dialog.getContext();
                Try.andHandle(project, AutomateBundle.message("action.InstallToolkit.InstallToolkit.Progress.Title"),
                              () -> application.installToolkit(context.ToolkitLocation),
                              this.onPerformed,
                              AutomateBundle.message("action.InstallToolkit.InstallToolkit.Failure.Message"));
            }
        }
    }
}
