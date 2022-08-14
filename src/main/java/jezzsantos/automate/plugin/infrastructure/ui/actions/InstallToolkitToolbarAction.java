package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.InstallToolkitDialog;
import org.jetbrains.annotations.NotNull;

public class InstallToolkitToolbarAction extends AnAction {

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        var message = AutomateBundle.message("action.InstallToolkit.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setIcon(AllIcons.Actions.Install);

        boolean isDraftEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isDraftEditingMode = application.getEditingMode() == EditingMode.Drafts;
        }
        presentation.setEnabledAndVisible(isDraftEditingMode);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            var dialog = new InstallToolkitDialog(project);
            if (dialog.showAndGet()) {
                var location = dialog.ToolkitLocation;
                try {
                    application.installToolkit(location);
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to install toolkit", ex);
                }
            }
        }
    }
}