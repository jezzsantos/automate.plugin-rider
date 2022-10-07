package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ViewPatternAction extends AnAction {

    @NotNull
    private final String name;
    @Nullable
    private final String id;

    private final Runnable onPerformed;

    public ViewPatternAction(@NotNull Runnable onPerformed) {

        this(onPerformed, "", null);
    }

    public ViewPatternAction(@NotNull Runnable onPerformed, @NotNull String name, @Nullable String id) {

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
            var currentPattern = Try.andHandle(project,
                                               application::getCurrentPatternInfo,
                                               AutomateBundle.message("action.PatternListItem.GetCurrentPattern.Failure.Message"));
            var isCurrentPattern = currentPattern != null && currentPattern.getId().equals(this.id);
            presentation.setIcon(isCurrentPattern
                                   ? AllIcons.Actions.Checked
                                   : null);
        }
        presentation.setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        if (this.id != null) {
            var project = e.getProject();
            if (project != null) {
                var application = IAutomateApplication.getInstance(project);
                Try.andHandle(project,
                              () -> application.setCurrentPattern(this.id),
                              this.onPerformed,
                              AutomateBundle.message("action.PatternListItem.SetCurrentPattern.Message"));
            }
        }
    }
}
