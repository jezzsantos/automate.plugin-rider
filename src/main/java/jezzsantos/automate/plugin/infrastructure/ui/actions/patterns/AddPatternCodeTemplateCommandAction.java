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

public class AddPatternCodeTemplateCommandAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public AddPatternCodeTemplateCommandAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.AddPatternCodeTemplateCommand.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isPatternEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isPatternEditingMode = application.getEditingMode() == EditingMode.PATTERNS;
        }

        var isPatternElementSite = Selection.isChildElementOrRootOrAutomationPlaceholder(e) != null;
        presentation.setEnabledAndVisible(isPatternEditingMode && isPatternElementSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.pattern.codetemplatecommand.add", null);

        var parent = Selection.isChildElementOrRootOrAutomationPlaceholder(e);
        if (parent != null) {
            var project = e.getProject();
            if (project != null) {
                var application = IAutomateApplication.getInstance(project);
                var codeTemplates = parent.getCodeTemplates();
                var automations = parent.getAutomation();
                var dialog = new EditPatternCodeTemplateCommandDialog(project, new EditPatternCodeTemplateCommandDialog.EditPatternCodeTemplateCommandDialogContext(codeTemplates,
                                                                                                                                                                    automations));
                if (dialog.showAndGet()) {
                    var context = dialog.getContext();
                    var automation = Try.andHandle(project,
                                                   () -> application.addPatternCodeTemplateCommand(parent.getEditPath(), context.getName(), context.getCodeTemplate().getName(),
                                                                                                   context.getTargetPath(), context.getIsOneOff()),
                                                   AutomateBundle.message("action.AddPatternCodeTemplateCommand.NewCodeTemplateCommand.Failure.Message"));
                    if (automation != null) {
                        this.onSuccess.run(model -> model.insertAutomation(automation));
                    }
                }
            }
        }
    }
}
