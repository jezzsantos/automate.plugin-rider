package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DraftsListToolbarAction extends ComboBoxAction {

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        String message = AutomateBundle.message("action.DraftsListToolbarAction.NoSelected.Title");
        boolean isDraftEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isDraftEditingMode = application.getEditingMode() == EditingMode.Drafts;
            var currentDraft = application.getCurrentDraft();
            if (currentDraft != null) {
                message = currentDraft.getName();
            }
        }

        var presentation = e.getPresentation();
        presentation.setDescription(AutomateBundle.message("action.DraftsListToolbarAction.Title"));
        presentation.setText(message);
        presentation.setEnabledAndVisible(isDraftEditingMode);
    }

    @Override
    protected @NotNull DefaultActionGroup createPopupActionGroup(JComponent component) {
        final var actions = new DefaultActionGroup();

        var project = DataManager.getInstance().getDataContext(component).getData(CommonDataKeys.PROJECT);
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            var drafts = application.getDrafts();
            var isAnyDrafts = !drafts.isEmpty();
            if (isAnyDrafts) {
                var isNoCurrentDraft = drafts.stream()
                        .noneMatch(DraftDefinition::getIsCurrent);
                if (isNoCurrentDraft) {
                    actions.add(new DraftListItemAction());
                }
                for (var draft : drafts) {
                    actions.add(new DraftListItemAction(draft.getName(), draft.getId()));
                }
            }
        }

        return actions;
    }
}
