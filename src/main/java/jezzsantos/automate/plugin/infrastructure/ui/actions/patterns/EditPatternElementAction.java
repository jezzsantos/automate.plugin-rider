package jezzsantos.automate.plugin.infrastructure.ui.actions.patterns;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.patterns.EditPatternElementDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternTreeModel;
import org.jetbrains.annotations.NotNull;

public class EditPatternElementAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public EditPatternElementAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.EditPatternElement.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isPatternEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isPatternEditingMode = application.getEditingMode() == EditingMode.PATTERNS;
        }

        var isElementSite = Selection.isElementOrPattern(e) != null;
        presentation.setEnabledAndVisible(isPatternEditingMode && isElementSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.pattern.element.edit", null);

        var project = e.getProject();
        if (project != null) {
            var selected = Selection.isElementOrPattern(e);
            if (selected != null) {
                var application = IAutomateApplication.getInstance(project);
                var attributes = selected.getParent().getAttributes();
                var elements = selected.getParent().getElements();
                var dialog = new EditPatternElementDialog(project, new EditPatternElementDialog.EditPatternElementDialogContext(selected.getElement(), attributes, elements));
                if (dialog.showAndGet()) {
                    var context = dialog.getContext();
                    if (selected.getElement().isRoot()) {
                        var pattern = Try.andHandle(project, AutomateBundle.message("action.EditPatternElement.UpdateRoot.Progress.Title"),
                                                    () -> application.updatePattern(context.getName(), context.getDisplayName(), context.getDescription()),
                                                    AutomateBundle.message("action.EditPatternElement.UpdateRoot.Failure.Message"));
                        if (pattern != null) {
                            this.onSuccess.run(model -> model.updatePattern(pattern));
                        }
                    }
                    else {
                        var element = Try.andHandle(project, AutomateBundle.message("action.EditPatternElement.UpdateElement.Progress.Title"),
                                                    () -> application.updatePatternElement(selected.getParent().getEditPath(), context.getId(), context.getName(),
                                                                                           context.getIsCollection(),
                                                                                           context.getIsRequired(),
                                                                                           context.getDisplayName(), context.getDescription(), context.getIsAutoCreate()),
                                                    AutomateBundle.message("action.EditPatternElement.UpdateElement.Failure.Message"));
                        if (element != null) {
                            this.onSuccess.run(model -> model.updateElement(element));
                        }
                    }
                }
            }
        }
    }
}
