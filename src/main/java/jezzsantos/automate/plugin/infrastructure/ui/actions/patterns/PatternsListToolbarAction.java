package jezzsantos.automate.plugin.infrastructure.ui.actions.patterns;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicReference;

public class PatternsListToolbarAction extends ComboBoxAction {

    private final Runnable onPerformed;

    public PatternsListToolbarAction(@NotNull Runnable onPerformed) {

        super();
        this.onPerformed = onPerformed;
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        AtomicReference<String> message = new AtomicReference<>(AutomateBundle.message("action.PatternsListToolbar.NoSelected.Message"));
        boolean isInstalled = false;
        boolean isAuthoringMode = false;
        boolean isPatternEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isInstalled = application.isCliInstalled();
            isAuthoringMode = application.isAuthoringMode();
            isPatternEditingMode = application.getEditingMode() == EditingMode.PATTERNS;
            if (isInstalled) {
                var currentPattern = Try.andHandleWithoutProgress(project, AutomateBundle.message("action.PatternsListToolbar.GetCurrentPattern.Progress.Title"),
                                                                  application::getCurrentPatternInfo,
                                                                  AutomateBundle.message("action.PatternsListToolbar.GetCurrentPattern.Failure.Message"));
                if (currentPattern != null) {
                    message.set(currentPattern.getName());
                }
            }
        }

        var presentation = e.getPresentation();
        presentation.setDescription(AutomateBundle.message("action.PatternsListToolbar.Title"));
        presentation.setText(message.get());
        presentation.setEnabledAndVisible(isInstalled && isAuthoringMode && isPatternEditingMode);
    }

    @Override
    protected @NotNull DefaultActionGroup createPopupActionGroup(JComponent component) {

        final var actions = new DefaultActionGroup();

        var project = DataManager.getInstance().getDataContext(component).getData(CommonDataKeys.PROJECT);
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            if (application.isCliInstalled()) {
                var patterns = Try.andHandle(project, AutomateBundle.message("action.PatternsListToolbar.GetAllPatterns.Progress.Title"),
                                             application::listPatterns,
                                             AutomateBundle.message("action.PatternsListToolbar.ListAllPatterns.Failure.Message"));
                var isAnyPatterns = patterns != null && !patterns.isEmpty();
                if (isAnyPatterns) {
                    var isNoCurrentPattern = patterns.stream()
                      .noneMatch(PatternLite::getIsCurrent);
                    if (isNoCurrentPattern) {
                        actions.add(new ViewPatternAction(this.onPerformed));
                    }
                    for (var pattern : patterns) {
                        actions.add(new ViewPatternAction(this.onPerformed, pattern));
                    }
                }
            }
        }

        return actions;
    }
}
