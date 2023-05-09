package jezzsantos.automate.plugin.infrastructure.ui.actions.patterns;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.IContainer;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternTreeModel;
import org.jetbrains.annotations.NotNull;

public class EditPatternCodeTemplateContentAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public EditPatternCodeTemplateContentAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.EditPatternCodeTemplateContent.Title");
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

        IRecorder.getInstance().measureEvent("action.pattern.element.codetemplate.edit", null);

        var project = e.getProject();
        if (project != null) {
            var selected = Selection.isCodeTemplatePlaceholder(e);
            if (selected != null) {
                var application = IAutomateApplication.getInstance(project);
                var codeTemplateEditorPath = Try.andHandle(project,
                                                           () -> application.getPatternCodeTemplateContent(selected.getParent().getEditPath(), selected.getTemplate().getName()),
                                                           AutomateBundle.message("action.EditPatternCodeTemplateAction.Failure.Message"));
                if (codeTemplateEditorPath != null) {
                    if (IContainer.getFileEditor(project).openFile(codeTemplateEditorPath)) {
                        this.onSuccess.run(model -> model.updateElement(selected.getParent()));
                    }
                }
            }
        }
    }
}
