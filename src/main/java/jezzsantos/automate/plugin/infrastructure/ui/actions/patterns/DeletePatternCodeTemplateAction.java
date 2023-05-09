package jezzsantos.automate.plugin.infrastructure.ui.actions.patterns;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.ConfirmDeleteDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternTreeModel;
import org.jetbrains.annotations.NotNull;

public class DeletePatternCodeTemplateAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public DeletePatternCodeTemplateAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.DeletePatternCodeTemplate.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isPatternEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isPatternEditingMode = application.getEditingMode() == EditingMode.PATTERNS;
        }

        var isCodeTemplateSite = Selection.isCodeTemplatePlaceholder(e) != null;
        presentation.setEnabledAndVisible(isPatternEditingMode && isCodeTemplateSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.pattern.codetemplate.delete", null);

        var project = e.getProject();
        if (project != null) {
            var selected = Selection.isCodeTemplatePlaceholder(e);
            if (selected != null) {
                if (ConfirmDeleteDialog.confirms(project,
                                                 AutomateBundle.message("dialog.ConfirmDelete.PatternCodeTemplate.Title"),
                                                 AutomateBundle.message("dialog.ConfirmDelete.PatternCodeTemplate.Message"))) {
                    var application = IAutomateApplication.getInstance(project);
                    Try.andHandle(project,
                                  () -> application.deletePatternCodeTemplate(selected.getParent().getEditPath(), selected.getTemplate().getName()),
                                  () -> this.onSuccess.run(model -> model.deleteCodeTemplate(selected.getTemplate())),
                                  AutomateBundle.message("action.DeletePatternCodeTemplate.DeleteCodeTemplate.Failure.Message"));
                }
            }
        }
    }
}
