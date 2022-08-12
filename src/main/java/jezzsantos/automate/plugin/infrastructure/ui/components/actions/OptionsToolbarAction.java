package jezzsantos.automate.plugin.infrastructure.ui.components.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.settings.ProjectSettingsConfigurable;
import org.jetbrains.annotations.NotNull;

public class OptionsToolbarAction extends AnAction {

    public OptionsToolbarAction() {
        super();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        var message = AutomateBundle.message("action.ShowSettings.Title");
        var presentation = e.getPresentation();
        presentation.setText(message);
        presentation.setIcon(AllIcons.General.GearPlain);
        presentation.setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getProject();
        if (project != null)
            ShowSettingsUtil.getInstance().showSettingsDialog(project, ProjectSettingsConfigurable.class);
    }
}
