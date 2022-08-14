package jezzsantos.automate.plugin.infrastructure.ui.components.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.components.dialogs.NewDraftDialog;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class AddDraftAction extends AnAction {

    private final Consumer<DraftDefinition> onSelect;

    public AddDraftAction(Consumer<DraftDefinition> onSelect) {
        super();
        this.onSelect = onSelect;
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        var message = AutomateBundle.message("action.AddDraft.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setIcon(AllIcons.General.Add);

        boolean isDraftEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isDraftEditingMode = application.getEditingMode() == EditingMode.Drafts;
        }
        presentation.setEnabledAndVisible(isDraftEditingMode);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            var drafts = application.getDrafts();
            var toolkits = application.getToolkits();
            var dialog = new NewDraftDialog(project, toolkits, drafts);
            if (dialog.showAndGet()) {
                var name = dialog.Name;
                var toolkitName = dialog.ToolkitName;
                DraftDefinition draft;
                try {
                    draft = application.createDraft(toolkitName, name);
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to add new draft", ex);
                }
                onSelect.accept(draft);
            }
        }
    }
}
