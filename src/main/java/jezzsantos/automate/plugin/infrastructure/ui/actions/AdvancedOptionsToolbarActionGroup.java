package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Separator;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdvancedOptionsToolbarActionGroup extends ActionGroup {

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        var message = AutomateBundle.message("action.ShowSettings.Title");
        var presentation = e.getPresentation();
        presentation.setText(message);
        presentation.setIcon(AllIcons.General.GearPlain);

        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            var isAuthoringMode = application.isAuthoringMode();
            presentation.setEnabledAndVisible(!isAuthoringMode);
        }
    }

    @NotNull
    @Override
    public AnAction[] getChildren(@Nullable AnActionEvent e) {
        return new AnAction[]{
                new ToggleAuthoringModeMenuAction(),
                new Separator(),
                new ShowSettingsMenuAction()
        };
    }

    @Override
    public boolean isPopup() {
        return true;
    }
}
