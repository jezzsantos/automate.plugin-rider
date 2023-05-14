package jezzsantos.automate.plugin.infrastructure.ui.actions.patterns;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.patterns.EditPatternCodeTemplateCommandDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternTreeModel;
import org.jetbrains.annotations.NotNull;

public class EditPatternCodeTemplateCommandAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public EditPatternCodeTemplateCommandAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.EditPatternCodeTemplateCommand.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isPatternEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isPatternEditingMode = application.getEditingMode() == EditingMode.PATTERNS;
        }

        var isAutomationSite = Selection.isCodeTemplateCommand(e) != null;
        presentation.setEnabledAndVisible(isPatternEditingMode && isAutomationSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.pattern.codetemplatecommand.edit", null);

        var project = e.getProject();
        if (project != null) {
            var selected = Selection.isCodeTemplateCommand(e);
            if (selected != null) {
                var application = IAutomateApplication.getInstance(project);
                var codeTemplates = selected.getParent().getCodeTemplates();
                var automations = selected.getParent().getAutomation();
                var dialog = new EditPatternCodeTemplateCommandDialog(project,
                                                                      new EditPatternCodeTemplateCommandDialog.EditPatternCodeTemplateCommandDialogContext(selected.getAutomation(),
                                                                                                                                                           codeTemplates,
                                                                                                                                                           automations));
                if (dialog.showAndGet()) {
                    var context = dialog.getContext();
                    var command = Try.andHandle(project,
                                                () -> application.updatePatternCodeTemplateCommand(selected.getParent().getEditPath(), context.getId(), context.getName(),
                                                                                                   context.getTargetPath(), context.getIsOneOff()),
                                                AutomateBundle.message("action.EditPatternCodeTemplateCommand.UpdateCommand.Failure.Message"));
                    if (command != null) {
                        this.onSuccess.run(model -> model.updateAutomation(command));
                    }
                }
            }
        }
    }
}
