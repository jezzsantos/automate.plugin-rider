package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.ExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PatternListItemAction extends AnAction {

    @NotNull
    private final String name;
    @Nullable
    private final String id;

    private final Runnable onPerformed;

    public PatternListItemAction(@NotNull Runnable onPerformed) {

        this(onPerformed, "", null);
    }

    public PatternListItemAction(@NotNull Runnable onPerformed, @NotNull String name, @Nullable String id) {

        super();
        this.onPerformed = onPerformed;
        this.name = name;
        this.id = id;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);
        var message = this.name;
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            var currentPattern = application.getCurrentPatternInfo();
            var isCurrentPattern = currentPattern != null && currentPattern.getId().equals(this.id);
            presentation.setIcon(isCurrentPattern
                                   ? AllIcons.Actions.Checked
                                   : null);
            presentation.setEnabledAndVisible(true);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        if (this.id != null) {
            var project = e.getProject();
            if (project != null) {
                var application = IAutomateApplication.getInstance(project);
                try {
                    application.setCurrentPattern(this.id);
                    this.onPerformed.run();
                } catch (Exception ex) {
                    ExceptionHandler.handle(project, ex, AutomateBundle.message("action.PatternsListToolbarItem.FailureNotification.Title"));
                }
            }
        }
    }
}
