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

public class AddPatternElementAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public AddPatternElementAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.AddPatternElement.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isPatternEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isPatternEditingMode = application.getEditingMode() == EditingMode.PATTERNS;
        }

        var isPatternSite = Selection.isChildElementOrRootOrElementPlaceholder(e) != null;
        presentation.setEnabledAndVisible(isPatternEditingMode && isPatternSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.pattern.element.add", null);

        var parent = Selection.isChildElementOrRootOrElementPlaceholder(e);
        if (parent != null) {
            var project = e.getProject();
            if (project != null) {
                var application = IAutomateApplication.getInstance(project);
                var attributes = parent.getAttributes();
                var elements = parent.getElements();
                var dialog = new EditPatternElementDialog(project,
                                                          new EditPatternElementDialog.EditPatternElementDialogContext(attributes, elements));
                if (dialog.showAndGet()) {
                    var context = dialog.getContext();
                    var element = Try.andHandle(project,
                                                () -> application.addPatternElement(parent.getEditPath(), context.getId(), context.getIsCollection(), context.getIsRequired(),
                                                                                    context.getDisplayName(), context.getDescription(), context.getIsAutoCreate()),
                                                AutomateBundle.message("action.AddPatternElement.NewElement.Failure.Message"));
                    if (element != null) {
                        this.onSuccess.run(model -> model.insertElement(element));
                    }
                }
            }
        }
    }
}
