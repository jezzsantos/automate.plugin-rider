package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ToggleAuthoringModeToolbarAction extends ToggleAction {

    private final Runnable onPerformed;
    private boolean selected;

    public ToggleAuthoringModeToolbarAction(@NotNull Runnable onPerformed) {

        super();
        this.onPerformed = onPerformed;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {

        return ActionUpdateThread.EDT;
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent anActionEvent) {

        return this.selected;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean selected) {

        this.selected = !this.selected;

        SetPresentation(e);

        IRecorder.getInstance().measureEvent("action.display.authoring-mode.show", Map.of(
          "Value", Boolean.toString(this.selected)
        ));

        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            application.setAuthoringMode(this.selected);
        }
        this.onPerformed.run();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            this.selected = application.isAuthoringMode();
        }

        super.update(e);

        SetPresentation(e);
    }

    @SuppressWarnings("DialogTitleCapitalization")
    private void SetPresentation(@NotNull AnActionEvent e) {

        var message = AutomateBundle.message("action.ToggleAuthoringMode.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setIcon(AllIcons.Actions.EditScheme);

        boolean isInstalled = false;
        boolean isAuthoringMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isInstalled = application.isCliInstalled();
            isAuthoringMode = application.isAuthoringMode();
        }
        presentation.setEnabledAndVisible(isInstalled && isAuthoringMode);
    }
}
