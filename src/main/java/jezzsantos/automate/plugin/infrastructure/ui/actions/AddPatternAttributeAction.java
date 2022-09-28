package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.EditPatternAttributeDialog;
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
            isPatternEditingMode = application.getEditingMode() == EditingMode.PATTERNS;
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
                var dialog = new EditPatternAttributeDialog(project,
                                                            new EditPatternAttributeDialog.EditPatternAttributeDialogContext(attributes, AutomateConstants.AttributeDataTypes));
                if (dialog.showAndGet()) {
                    var context = dialog.getContext();
                    var attribute = Try.andHandle(project,
                                                  () -> application.addPatternAttribute(parent.getEditPath(), context.getId(), context.getIsRequired(), context.getDataType(),
                                                                                        context.getDefaultValue(), context.getChoices()),
                                                  AutomateBundle.message("action.AddPatternAttribute.NewAttribute.Failure.Message"));
                    if (attribute != null) {
                        this.onSuccess.run(model -> model.insertAttribute(attribute));
                    }
                }
            }
        }
    }

    private PatternElement getParentElement(AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof PatternElement) {
                    return (PatternElement) leaf;
                }
                else {
                    if (leaf instanceof PatternFolderPlaceholderNode placeholder) {
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
