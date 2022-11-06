package jezzsantos.automate.plugin.infrastructure.ui.actions.drafts;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.DraftTreeModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class ListDraftElementsActionGroup extends ActionGroup {

    private final Action<DraftTreeModel> onSuccess;

    public ListDraftElementsActionGroup(Action<DraftTreeModel> onSuccess) {

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
            isDraftEditingMode = application.getEditingMode() == EditingMode.DRAFTS;
        }

        var isElementSite = Selection.isChildElementOrRoot(e) != null;
        var presentation = e.getPresentation();
        presentation.setEnabledAndVisible(isDraftEditingMode && isElementSite);
    }

    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {

        if (e != null) {
            var parentPlaceholder = Selection.isChildElementOrRoot(e);
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
}
