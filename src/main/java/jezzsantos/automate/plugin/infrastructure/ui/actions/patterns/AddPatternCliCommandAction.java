package jezzsantos.automate.plugin.infrastructure.ui.actions.patterns;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.patterns.EditPatternCliCommandDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternTreeModel;
import org.jetbrains.annotations.NotNull;

public class AddPatternCliCommandAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public AddPatternCliCommandAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.AddPatternCliCommand.Title");
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

        IRecorder.getInstance().measureEvent("action.pattern.clicommand.add", null);

        var parent = Selection.isChildElementOrRootOrAutomationPlaceholder(e);
        if (parent != null) {
            var project = e.getProject();
            if (project != null) {
                var application = IAutomateApplication.getInstance(project);
                var automations = parent.getAutomation();
                var dialog = new EditPatternCliCommandDialog(project, new EditPatternCliCommandDialog.EditPatternCliCommandDialogContext(automations));
                if (dialog.showAndGet()) {
                    var context = dialog.getContext();
                    var automation = Try.andHandle(project, AutomateBundle.message("action.AddPatternCliCommand.NewCliCommand.Progress.Title"),
                                                   () -> application.addPatternCliCommand(parent.getEditPath(), context.getName(), context.getApplicationName(),
                                                                                          context.getArguments()),
                                                   AutomateBundle.message("action.AddPatternCliCommand.NewCliCommand.Failure.Message"));
                    if (automation != null) {
                        this.onSuccess.run(model -> model.insertAutomation(automation));
                    }
                }
            }
        }
    }
}
