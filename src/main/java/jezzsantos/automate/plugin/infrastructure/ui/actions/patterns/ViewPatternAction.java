package jezzsantos.automate.plugin.infrastructure.ui.actions.patterns;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ViewPatternAction extends AnAction {

    @Nullable
    private final PatternLite pattern;

    private final Runnable onPerformed;

    public ViewPatternAction(@NotNull Runnable onPerformed) {

        this(onPerformed, null);
    }

    public ViewPatternAction(@NotNull Runnable onPerformed, @Nullable PatternLite pattern) {

        super();
        this.onPerformed = onPerformed;
        this.pattern = pattern;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);
        var message = this.pattern == null
          ? ""
          : this.pattern.getName();
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            var currentPattern = Try.andHandleWithoutProgress(project, AutomateBundle.message("action.ViewPattern.GetCurrentPattern.Progress.Title"),
                                                              application::getCurrentPatternInfo,
                                                              AutomateBundle.message("action.ViewPattern.GetCurrentPattern.Failure.Message"));
            var isThisPatternCurrentPattern = currentPattern != null && currentPattern.getId().equals(this.pattern == null
                                                                                                        ? null
                                                                                                        : this.pattern.getId());
            presentation.setIcon(isThisPatternCurrentPattern
                                   ? AllIcons.Actions.Checked
                                   : null);
        }
        presentation.setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.pattern.view", null);

        if (this.pattern != null) {
            var project = e.getProject();
            if (project != null) {
                var application = IAutomateApplication.getInstance(project);
                Try.andHandle(project, AutomateBundle.message("action.ViewPattern.SetCurrentPattern.Progress.Title"),
                              () -> application.setCurrentPattern(this.pattern.getId()),
                              this.onPerformed,
                              AutomateBundle.message("action.ViewPattern.SetCurrentPattern.Message"));
            }
        }
    }
}
