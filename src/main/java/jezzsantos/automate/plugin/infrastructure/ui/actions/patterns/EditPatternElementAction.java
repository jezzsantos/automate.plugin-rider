package jezzsantos.automate.plugin.infrastructure.ui.actions.patterns;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.patterns.EditPatternElementDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternTreeModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

public class EditPatternElementAction extends AnAction {

    private final Action<PatternTreeModel> onSuccess;

    public EditPatternElementAction(Action<PatternTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        var message = AutomateBundle.message("action.EditPatternElement.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);

        boolean isPatternEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isPatternEditingMode = application.getEditingMode() == EditingMode.PATTERNS;
        }

        var isElementSite = getSelection(e) != null;
        presentation.setEnabledAndVisible(isPatternEditingMode && isElementSite);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.pattern.element.edit", null);

        var project = e.getProject();
        if (project != null) {
            var selected = getSelection(e);
            if (selected != null) {
                var application = IAutomateApplication.getInstance(project);
                var attributes = selected.getParent().getAttributes();
                var elements = selected.getParent().getElements();
                var dialog = new EditPatternElementDialog(project, new EditPatternElementDialog.EditPatternElementDialogContext(selected.getElement(), attributes, elements));
                if (dialog.showAndGet()) {
                    var context = dialog.getContext();
                    if (selected.element.isRoot()) {
                        var pattern = Try.andHandle(project,
                                                    () -> application.updatePattern(context.getName(), context.getDisplayName(), context.getDescription()),
                                                    AutomateBundle.message("action.EditPatternElement.UpdateRoot.Failure.Message"));
                        if (pattern != null) {
                            this.onSuccess.run(model -> model.updatePattern(pattern));
                        }
                    }
                    else {
                        var element = Try.andHandle(project,
                                                    () -> application.updatePatternElement(selected.getParent().getEditPath(), context.getId(), context.getName(),
                                                                                           context.getIsCollection(),
                                                                                           context.getIsRequired(),
                                                                                           context.getDisplayName(), context.getDescription(), context.getIsAutoCreate()),
                                                    AutomateBundle.message("action.EditPatternElement.UpdateElement.Failure.Message"));
                        if (element != null) {
                            this.onSuccess.run(model -> model.updateElement(element));
                        }
                    }
                }
            }
        }
    }

    private SelectedElement getSelection(AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof PatternElement patternElement) {
                    if (patternElement.isRoot()) {
                        return new SelectedElement(patternElement, patternElement);
                    }
                    else {
                        var parent = path.getParentPath().getParentPath().getLastPathComponent();
                        if (parent instanceof PatternElement parentElement) {
                            return new SelectedElement(parentElement, (PatternElement) leaf);
                        }
                    }
                }
            }
        }

        return null;
    }

    static class SelectedElement {

        private final PatternElement element;
        private final PatternElement parent;

        public SelectedElement(@NotNull PatternElement parent, @NotNull PatternElement element) {

            this.parent = parent;
            this.element = element;
        }

        public PatternElement getParent() {return this.parent;}

        public PatternElement getElement() {return this.element;}
    }
}
