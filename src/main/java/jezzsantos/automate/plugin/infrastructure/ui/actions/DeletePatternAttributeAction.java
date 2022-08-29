package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.ExceptionHandler;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.ConfirmDeleteDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternTreeModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

public class DeletePatternAttributeAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public DeletePatternAttributeAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.DeletePatternAttribute.Title");
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
            var selected = getAttribute(e);
            if (selected != null) {
                if (ConfirmDeleteDialog.confirms(project,
                                                 AutomateBundle.message("dialog.ConfirmDelete.PatternAttribute.Title"),
                                                 AutomateBundle.message("dialog.ConfirmDelete.PatternAttribute.Message"))) {
                    var application = IAutomateApplication.getInstance(project);
                    try {
                        application.deletePatternAttribute(selected.parent.getEditPath(), selected.attribute.getName());
                        this.onSuccess.run(model -> model.deleteAttribute(selected.attribute));
                    } catch (Exception ex) {
                        ExceptionHandler.handle(project, ex, AutomateBundle.message("action.DeletePatternAttribute.FailureNotification.Title"));
                    }
                }
            }
        }
    }

    private SelectedAttribute getAttribute(AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath) {
                var path = (TreePath) selection;
                var leaf = path.getLastPathComponent();
                if (leaf instanceof Attribute) {
                    var parent = path.getParentPath().getParentPath().getLastPathComponent();
                    if (parent instanceof PatternElement) {
                        var parentElement = (PatternElement) parent;
                        return new SelectedAttribute(parentElement, (Attribute) leaf);
                    }
                }
            }
        }

        return null;
    }

    private static class SelectedAttribute {

        private final Attribute attribute;
        private final PatternElement parent;

        public SelectedAttribute(@NotNull PatternElement parent, @NotNull Attribute attribute) {

            this.parent = parent;
            this.attribute = attribute;
        }
    }
}
