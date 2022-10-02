package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.EditPatternAttributeDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternTreeModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

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

        var isAttributeSite = getSelection(e) != null;
        presentation.setEnabledAndVisible(isPatternEditingMode && isAttributeSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        var project = e.getProject();
        if (project != null) {
            var selected = getSelection(e);
            if (selected != null) {
                var application = IAutomateApplication.getInstance(project);
                var attributes = selected.getParent().getAttributes();
                var dialog = new EditPatternAttributeDialog(project,
                                                            new EditPatternAttributeDialog.EditPatternAttributeDialogContext(selected.getAttribute(), attributes,
                                                                                                                             AutomateConstants.AttributeDataTypes));
                if (dialog.showAndGet()) {
                    var context = dialog.getContext();
                    var attribute = Try.andHandle(project,
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

    private SelectedAttribute getSelection(AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof Attribute) {
                    var parent = path.getParentPath().getParentPath().getLastPathComponent();
                    if (parent instanceof PatternElement parentElement) {
                        return new SelectedAttribute(parentElement, (Attribute) leaf);
                    }
                }
            }
        }

        return null;
    }

    static class SelectedAttribute {

        private final Attribute attribute;
        private final PatternElement parent;

        public SelectedAttribute(@NotNull PatternElement parent, @NotNull Attribute attribute) {

            this.parent = parent;
            this.attribute = attribute;
        }

        public PatternElement getParent() {return this.parent;}

        public Attribute getAttribute() {return this.attribute;}
    }
}
