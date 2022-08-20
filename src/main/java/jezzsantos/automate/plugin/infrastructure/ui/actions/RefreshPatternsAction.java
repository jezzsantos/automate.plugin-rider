package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;

public class RefreshPatternsAction extends AnAction {
    private final Runnable onPerformed;

    public RefreshPatternsAction(Runnable onPerformed) {
        super();
        this.onPerformed = onPerformed;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        var message = AutomateBundle.message("action.RefreshPatterns.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setIcon(AllIcons.Actions.Refresh);
        presentation.setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            application.refreshLocalState();
        }
        onPerformed.run();
    }
}
