package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ToggleAuthoringModeMenuAction extends AnAction {

    private final Runnable onPerformed;

    public ToggleAuthoringModeMenuAction(@NotNull Runnable onPerformed) {

        super();
        this.onPerformed = onPerformed;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {

        return ActionUpdateThread.EDT;
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.TurnOnAuthoringMode.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.display.authoring-mode.change", Map.of(
          "Value", Boolean.toString(true)
        ));

        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            application.setAuthoringMode(true);
            this.onPerformed.run();
        }
    }
}
