package jezzsantos.automate.plugin.infrastructure.ui.actions.patterns;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.patterns.EditPatternCommandLaunchPointDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternTreeModel;
import org.jetbrains.annotations.NotNull;

public class AddPatternCommandLaunchPointAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public AddPatternCommandLaunchPointAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.AddPatternCommandLaunchPoint.Title");
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

        IRecorder.getInstance().measureEvent("action.pattern.commandlaunchpoint.add", null);

        var parent = Selection.isChildElementOrRootOrAutomationPlaceholder(e);
        if (parent != null) {
            var project = e.getProject();
            if (project != null) {
                var application = IAutomateApplication.getInstance(project);
                var automations = parent.getAutomation();
                var dialog = new EditPatternCommandLaunchPointDialog(project, new EditPatternCommandLaunchPointDialog.EditPatternCommandLaunchPointDialogContext(automations));
                if (dialog.showAndGet()) {
                    var context = dialog.getContext();
                    var automation = Try.andHandle(project, AutomateBundle.message("action.AddPatternCommandLaunchPoint.NewCommandLaunchPoint.Progress.Title"),
                                                   () -> application.addPatternCommandLaunchPoint(parent.getEditPath(), context.getName(), context.getAddIdentifiers(),
                                                                                                  context.getFrom()),
                                                   AutomateBundle.message("action.AddPatternCommandLaunchPoint.NewCommandLaunchPoint.Failure.Message"));
                    if (automation != null) {
                        this.onSuccess.run(model -> model.insertAutomation(automation));
                    }
                }
            }
        }
    }
}
