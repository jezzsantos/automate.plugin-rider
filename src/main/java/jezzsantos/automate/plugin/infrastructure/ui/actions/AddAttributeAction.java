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
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.NewAttributeDialog;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.NewAttributeDialogContext;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternTreeModel;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.TreePlaceholder;
import org.jetbrains.annotations.NotNull;

public class AddAttributeAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public AddAttributeAction(Action<PatternTreeModel> onSuccess) {
        super();
        this.onSuccess = onSuccess;
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        var message = AutomateBundle.message("action.AddAttribute.Title");
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
                var dialog = new NewAttributeDialog(project, new NewAttributeDialogContext(attributes, AutomateConstants.AttributeDataTypes));
                if (dialog.showAndGet()) {
                    var context = dialog.getContext();
                    try {
                        var attribute = application.addAttribute(context.Name, context.IsRequired, context.DataType, context.DefaultValue, context.Choices);
                        this.onSuccess.run(model -> model.insertAttribute(attribute));
                    } catch (Exception ex) {
                        throw new RuntimeException("Failed to create new attribute", ex);
                    }
                }
            }
        }
    }

    private PatternElement getParentElement(AnActionEvent e) {
        var data = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (data instanceof PatternElement) {
            return (PatternElement) data;
        }
        else {
            if (data instanceof TreePlaceholder) {
                var placeholder = (TreePlaceholder) data;
                return (placeholder.getChild() == placeholder.getParent().getAttributes())
                        ? placeholder.getParent()
                        : null;
            }
        }

        return null;
    }
}
