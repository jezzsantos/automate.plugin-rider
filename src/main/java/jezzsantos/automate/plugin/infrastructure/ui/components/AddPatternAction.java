package jezzsantos.automate.plugin.infrastructure.ui.components;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class AddPatternAction extends AnAction {

    private final List<PatternDefinition> patterns;
    private final Consumer<PatternDefinition> onSelect;


    public AddPatternAction(List<PatternDefinition> patterns, Consumer<PatternDefinition> onSelect) {
        super();
        this.patterns = patterns;
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
        presentation.setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            var dialog = new NewPatternDialog(project, this.patterns);
            if (dialog.showAndGet()) {
                var name = dialog.Name;
                var pattern = new PatternDefinition(UUID.randomUUID().toString(), name, null, true);
                this.patterns.add(pattern);
                onSelect.accept(pattern);
            }
        }
    }
}
