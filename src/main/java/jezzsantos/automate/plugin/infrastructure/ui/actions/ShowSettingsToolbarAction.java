package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.options.ShowSettingsUtil;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.settings.ApplicationSettingsConfigurable;
import org.jetbrains.annotations.NotNull;

public class ShowSettingsToolbarAction extends AnAction {

    public ShowSettingsToolbarAction() {

        super();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.ShowSettings.Title");
        var presentation = e.getPresentation();
        presentation.setText(message);
        presentation.setIcon(AllIcons.General.GearPlain);

        boolean isInstalled = false;
        boolean isAuthoringMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isInstalled = application.isCliInstalled();
            isAuthoringMode = application.isAuthoringMode();
        }

        presentation.setEnabledAndVisible(!isInstalled || isAuthoringMode);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        var project = e.getProject();
        if (project != null) {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, ApplicationSettingsConfigurable.class);
        }
    }
}
