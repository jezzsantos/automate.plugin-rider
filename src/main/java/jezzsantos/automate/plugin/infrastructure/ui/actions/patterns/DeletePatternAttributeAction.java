package jezzsantos.automate.plugin.infrastructure.ui.actions.patterns;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
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
            isPatternEditingMode = application.getEditingMode() == EditingMode.PATTERNS;
        }

        var isAttributeSite = getSelection(e) != null;
        presentation.setEnabledAndVisible(isPatternEditingMode && isAttributeSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.pattern.attribute.delete", null);

        var project = e.getProject();
        if (project != null) {
            var selected = getSelection(e);
            if (selected != null) {
                if (ConfirmDeleteDialog.confirms(project,
                                                 AutomateBundle.message("dialog.ConfirmDelete.PatternAttribute.Title"),
                                                 AutomateBundle.message("dialog.ConfirmDelete.PatternAttribute.Message"))) {
                    var application = IAutomateApplication.getInstance(project);
                    Try.andHandle(project,
                                  () -> application.deletePatternAttribute(selected.getParent().getEditPath(), selected.getAttribute().getName()),
                                  () -> this.onSuccess.run(model -> model.deleteAttribute(selected.getAttribute())),
                                  AutomateBundle.message("action.DeletePatternAttribute.DeleteAttribute.Failure.Message"));
                }
            }
        }
    }

    private EditPatternAttributeAction.SelectedAttribute getSelection(AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof Attribute) {
                    var parent = path.getParentPath().getParentPath().getLastPathComponent();
                    if (parent instanceof PatternElement parentElement) {
                        return new EditPatternAttributeAction.SelectedAttribute(parentElement, (Attribute) leaf);
                    }
                }
            }
        }

        return null;
    }
}
