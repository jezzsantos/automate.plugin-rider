package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.jetbrains.annotations.NotNull;

public class TogglePatternEditingModeAction extends ToggleAction {

    private final Runnable onPerformed;
    private boolean selected;

    public TogglePatternEditingModeAction(@NotNull Runnable onPerformed) {

        super();
        this.onPerformed = onPerformed;
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent anActionEvent) {

        return this.selected;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean selected) {

        this.selected = !this.selected;

        SetPresentation(e);

        var project = e.getProject();
        if (project != null) {
            if (selected) {
                var application = IAutomateApplication.getInstance(project);
                application.setEditingMode(EditingMode.PATTERNS);
                this.onPerformed.run();
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            this.selected = application.getEditingMode() == EditingMode.PATTERNS;
        }

        super.update(e);

        SetPresentation(e);
    }

    private void SetPresentation(@NotNull AnActionEvent e) {

        var message = AutomateBundle.message("action.EditingMode.Patterns.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setIcon(AllIcons.General.ProjectStructure);

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
