package jezzsantos.automate.ui.components;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import jezzsantos.automate.AutomateBundle;
import jezzsantos.automate.settings.ProjectSettingsConfigurable;
import org.jetbrains.annotations.NotNull;

public class OptionsToolbarAction extends AnAction {

    public OptionsToolbarAction() {
        super();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        String message = AutomateBundle.message("action.ShowSettings.Title");
        e.getPresentation().setText(message);
        e.getPresentation().setIcon(AllIcons.General.Gear);
        e.getPresentation().setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null)
            ShowSettingsUtil.getInstance().showSettingsDialog(project, ProjectSettingsConfigurable.class);
    }
}
