package jezzsantos.automate.plugin.infrastructure.ui.actions.patterns;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.patterns.EditPatternAttributeDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternTreeModel;
import org.jetbrains.annotations.NotNull;

public class EditPatternAttributeAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public EditPatternAttributeAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.EditPatternAttribute.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isPatternEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isPatternEditingMode = application.getEditingMode() == EditingMode.PATTERNS;
        }

        var isAttributeSite = Selection.isAttribute(e) != null;
        presentation.setEnabledAndVisible(isPatternEditingMode && isAttributeSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.pattern.attribute.edit", null);

        var project = e.getProject();
        if (project != null) {
            var selected = Selection.isAttribute(e);
            if (selected != null) {
                var application = IAutomateApplication.getInstance(project);
                var attributes = selected.getParent().getAttributes();
                var dialog = new EditPatternAttributeDialog(project,
                                                            new EditPatternAttributeDialog.EditPatternAttributeDialogContext(selected.getAttribute(), attributes,
                                                                                                                             AutomateConstants.AttributeDataTypes));
                if (dialog.showAndGet()) {
                    var context = dialog.getContext();
                    var attribute = Try.andHandle(project, AutomateBundle.message("action.EditPatternAttribute.UpdateAttribute.Progress.Title"),
                                                  () -> application.updatePatternAttribute(selected.getParent().getEditPath(), context.getId(), context.getName(),
                                                                                           context.getIsRequired(),
                                                                                           context.getDataType(), context.getDefaultValue(), context.getChoices()),
                                                  AutomateBundle.message("action.EditPatternAttribute.UpdateAttribute.Failure.Message"));
                    if (attribute != null) {
                        this.onSuccess.run(model -> model.updateAttribute(attribute));
                    }
                }
            }
        }
    }
}
