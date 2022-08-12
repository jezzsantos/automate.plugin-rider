package jezzsantos.automate.plugin.infrastructure.ui.components.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class AddPatternAction extends AnAction {

    private final Consumer<PatternDefinition> onSelect;

    public AddPatternAction(Consumer<PatternDefinition> onSelect) {
        super();
        this.onSelect = onSelect;
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        var message = AutomateBundle.message("action.AddPattern.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setIcon(AllIcons.General.Add);

        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            var isAuthoringMode = application.isAuthoringMode();
            presentation.setEnabledAndVisible(isAuthoringMode);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            var patterns = application.getPatterns();
            var dialog = new NewPatternDialog(project, patterns);
            if (dialog.showAndGet()) {
                var name = dialog.Name;
                PatternDefinition pattern;
                try {
                    pattern = application.addPattern(name);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                onSelect.accept(pattern);
            }
        }
    }
}
