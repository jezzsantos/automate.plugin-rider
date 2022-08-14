package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;

public class ToggleDraftEditingModeAction extends ToggleAction {
    private boolean selected;

    @Override
    public void update(@NotNull AnActionEvent e) {

        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            this.selected = application.getEditingMode() == EditingMode.Drafts;
        }

        super.update(e);

        SetPresentation(e);
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
                application.setEditingMode(EditingMode.Drafts);
            }
        }
    }

    private void SetPresentation(@NotNull AnActionEvent e) {
        var message = AutomateBundle.message("action.EditingMode.Drafts.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setIcon(AllIcons.Actions.QuickfixOffBulb);

        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            var isAuthoringMode = application.isAuthoringMode();
            presentation.setEnabledAndVisible(isAuthoringMode);
        }
    }
}
