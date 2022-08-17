package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.NewDraftDialog;
import org.jetbrains.annotations.NotNull;

public class AddDraftAction extends AnAction {
    private final Runnable onPerformed;

    public AddDraftAction(@NotNull Runnable onPerformed) {
        super();
        this.onPerformed = onPerformed;
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
            var drafts = application.listDrafts();
            var toolkits = application.listToolkits();
            var dialog = new NewDraftDialog(project, toolkits, drafts);
            if (dialog.showAndGet()) {
                var name = dialog.Name;
                var toolkitName = dialog.ToolkitName;
                try {
                    application.createDraft(toolkitName, name);
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to add new draft", ex);
                }
                onPerformed.run();
            }
        }
    }
}
