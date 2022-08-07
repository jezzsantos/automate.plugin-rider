package jezzsantos.automate.ui.components;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import jezzsantos.automate.AutomateBundle;
import org.jetbrains.annotations.NotNull;

public class AddPatternAction extends AnAction {

    public AddPatternAction() {
        super();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        String message = AutomateBundle.message("action.AddPattern.Title");
        e.getPresentation().setDescription(message);
        e.getPresentation().setText(message);
        e.getPresentation().setIcon(AllIcons.General.Add);
        e.getPresentation().setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            var dialog = new NewPatternDialog(project);
            if (dialog.showAndGet()) {
                var name = dialog.Name;

            }
        }
    }
}
