package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.DraftElementPlaceholderNode;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.DraftTreeModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Objects;

public class AddDraftElementListActionGroup extends ActionGroup {

    private final Action<DraftTreeModel> onSuccess;

    public AddDraftElementListActionGroup(Action<DraftTreeModel> onSuccess) {

        super();
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        boolean isDraftEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isDraftEditingMode = application.getEditingMode() == EditingMode.Drafts;
        }

        var isElementSite = getParentElement(e) != null;
        var presentation = e.getPresentation();
        presentation.setEnabledAndVisible(isDraftEditingMode && isElementSite);
    }

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {

        if (e != null) {
            var parentPlaceholder = getParentElement(e);
            if (parentPlaceholder != null) {
                var missingSchemas = Objects.requireNonNull(parentPlaceholder.getSchema()).listMissingElements(parentPlaceholder.getElement());
                if (!missingSchemas.isEmpty()) {
                    var actions = new ArrayList<AnAction>();
                    for (var missingSchema : missingSchemas) {
                        actions.add(new AddDraftElementAction(parentPlaceholder.getElement(), missingSchema, this.onSuccess));
                    }

                    return actions.toArray(new AnAction[0]);
                }
            }
        }

        return new AnAction[0];
    }

    private DraftElementPlaceholderNode getParentElement(AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath) {
                var path = (TreePath) selection;
                var leaf = path.getLastPathComponent();
                if (leaf instanceof DraftElementPlaceholderNode) {
                    return ((DraftElementPlaceholderNode) leaf);
                }
            }
        }

        return null;
    }
}
