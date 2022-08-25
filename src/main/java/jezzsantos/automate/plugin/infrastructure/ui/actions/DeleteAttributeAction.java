package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.ExceptionHandler;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.ConfirmDeleteDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternTreeModel;
import org.jetbrains.annotations.NotNull;

public class DeleteAttributeAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public DeleteAttributeAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.DeleteAttribute.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isPatternEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isPatternEditingMode = application.getEditingMode() == EditingMode.Patterns;
        }

        var isAttributeSite = getAttribute(e) != null;
        presentation.setEnabledAndVisible(isPatternEditingMode && isAttributeSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        var project = e.getProject();
        if (project != null) {
            var attribute = getAttribute(e);
            if (attribute != null) {
                if (ConfirmDeleteDialog.confirms(project,
                                                 AutomateBundle.message("dialog.ConfirmDelete.Attribute.Title"),
                                                 AutomateBundle.message("dialog.ConfirmDelete.Attribute.Message"))) {
                    var application = IAutomateApplication.getInstance(project);
                    try {
                        application.deleteAttribute(attribute.getName());
                        this.onSuccess.run(model -> model.deleteAttribute(attribute));
                    } catch (Exception ex) {
                        ExceptionHandler.handle(project, ex, AutomateBundle.message("action.DeleteAttribute.FailureNotification.Title"));
                    }
                }
            }
        }
    }

    private Attribute getAttribute(AnActionEvent e) {

        var data = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (data instanceof Attribute) {
            return (Attribute) data;
        }

        return null;
    }
}
