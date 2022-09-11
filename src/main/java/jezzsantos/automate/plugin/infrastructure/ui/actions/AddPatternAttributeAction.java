package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.ExceptionHandler;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.NewPatternAttributeDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternFolderPlaceholderNode;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternTreeModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

public class AddPatternAttributeAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public AddPatternAttributeAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.AddPatternAttribute.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isPatternEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isPatternEditingMode = application.getEditingMode() == EditingMode.Patterns;
        }

        var isAttributeSite = getParentElement(e) != null;
        presentation.setEnabledAndVisible(isPatternEditingMode && isAttributeSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        var parent = getParentElement(e);
        if (parent != null) {
            var project = e.getProject();
            if (project != null) {
                var application = IAutomateApplication.getInstance(project);
                var attributes = parent.getAttributes();
                var dialog = new NewPatternAttributeDialog(project,
                                                           new NewPatternAttributeDialog.NewPatternAttributeDialogContext(attributes, AutomateConstants.AttributeDataTypes));
                if (dialog.showAndGet()) {
                    var context = dialog.getContext();
                    try {
                        var attribute = application.addPatternAttribute(parent.getEditPath(), context.Name, context.IsRequired, context.DataType, context.DefaultValue,
                                                                        context.Choices);
                        this.onSuccess.run(model -> model.insertAttribute(attribute));
                    } catch (Exception ex) {
                        ExceptionHandler.handle(project, ex, AutomateBundle.message("action.AddPatternAttribute.FailureNotification.Title"));
                    }
                }
            }
        }
    }

    private PatternElement getParentElement(AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath) {
                var path = (TreePath) selection;
                var leaf = path.getLastPathComponent();
                if (leaf instanceof PatternElement) {
                    return (PatternElement) leaf;
                }
                else {
                    if (leaf instanceof PatternFolderPlaceholderNode) {
                        var placeholder = (PatternFolderPlaceholderNode) leaf;
                        return (placeholder.getChild() == placeholder.getParent().getAttributes())
                          ? placeholder.getParent()
                          : null;
                    }
                }
            }
        }

        return null;
    }
}
