package jezzsantos.automate.plugin.infrastructure.ui.actions.patterns;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.patterns.EditPatternCodeTemplateDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternTreeModel;
import org.jetbrains.annotations.NotNull;

public class AddPatternCodeTemplateAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public AddPatternCodeTemplateAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.AddPatternCodeTemplate.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isPatternEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isPatternEditingMode = application.getEditingMode() == EditingMode.PATTERNS;
        }

        var isPatternElementSite = Selection.isChildElementOrRootOrCodeTemplatePlaceholder(e) != null;
        presentation.setEnabledAndVisible(isPatternEditingMode && isPatternElementSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.pattern.codetemplate.add", null);

        var parent = Selection.isChildElementOrRootOrCodeTemplatePlaceholder(e);
        if (parent != null) {
            var project = e.getProject();
            if (project != null) {
                var application = IAutomateApplication.getInstance(project);
                var codeTemplates = parent.getCodeTemplates();
                var automations = parent.getAutomation();
                var dialog = new EditPatternCodeTemplateDialog(project, new EditPatternCodeTemplateDialog.EditPatternCodeTemplateDialogContext(codeTemplates, automations));
                if (dialog.showAndGet()) {
                    var context = dialog.getContext();
                    if (context.isAddCommand()) {
                        var codeTemplateAndCommand = Try.andHandle(project, AutomateBundle.message("action.AddPatternCodeTemplate.NewCodeTemplate.Progress.Title"),
                                                                   () -> application.addPatternCodeTemplateWithCommand(parent.getEditPath(), context.getName(),
                                                                                                                       context.getFilePath(), context.getCommandName(),
                                                                                                                       context.getCommandTargetPath(),
                                                                                                                       context.getCommandIsOneOff()),
                                                                   AutomateBundle.message("action.AddPatternCodeTemplate.NewCodeTemplate.Failure.Message"));
                        if (codeTemplateAndCommand != null) {
                            this.onSuccess.run(model -> {
                                model.insertCodeTemplate(codeTemplateAndCommand.getCodeTemplate(), codeTemplateAndCommand.getAutomation());
                                //TODO: open the template in editor
                            });
                        }
                    }
                    else {
                        var codeTemplate = Try.andHandle(project, AutomateBundle.message("action.AddPatternCodeTemplate.NewCodeTemplate.Progress.Title"),
                                                         () -> application.addPatternCodeTemplate(parent.getEditPath(), context.getName(), context.getFilePath()),
                                                         AutomateBundle.message("action.AddPatternCodeTemplate.NewCodeTemplate.Failure.Message"));
                        if (codeTemplate != null) {
                            this.onSuccess.run(model -> {
                                model.insertCodeTemplate(codeTemplate, null);
                                //TODO: open the template in editor
                            });
                        }
                    }
                }
            }
        }
    }
}
