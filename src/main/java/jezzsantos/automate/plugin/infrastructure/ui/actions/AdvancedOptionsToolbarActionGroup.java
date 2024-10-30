package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdvancedOptionsToolbarActionGroup extends ActionGroup {

    private final Runnable onPerformed;

    public AdvancedOptionsToolbarActionGroup(@NotNull Runnable onPerformed) {

        super();
        setPopup(true);
        this.onPerformed = onPerformed;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {

        return ActionUpdateThread.EDT;
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
        presentation.setEnabledAndVisible(isInstalled && !isAuthoringMode);
    }

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {

        return new AnAction[]{new ToggleAuthoringModeMenuAction(this.onPerformed), new Separator(), new ShowSettingsMenuAction()};
    }
}
