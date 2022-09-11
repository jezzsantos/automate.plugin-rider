package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DraftsListToolbarAction extends ComboBoxAction {

    private final Runnable onPerformed;

    public DraftsListToolbarAction(@NotNull Runnable onPerformed) {

        super();
        this.onPerformed = onPerformed;
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);

        String message = AutomateBundle.message("action.DraftsListToolbar.NoSelected.Message");
        boolean isInstalled = false;
        boolean isDraftEditingMode = false;
        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            isInstalled = application.isCliInstalled();
            isDraftEditingMode = application.getEditingMode() == EditingMode.Drafts;
            if (isInstalled) {
                var currentDraft = Try.andHandle(project, application::getCurrentDraftInfo, AutomateBundle.message("action.DraftsListToolbar.GetCurrentDraft.Failure.Message"));
                if (currentDraft != null) {
                    message = currentDraft.getName();
                }
            }
        }

        var presentation = e.getPresentation();
        presentation.setDescription(AutomateBundle.message("action.DraftsListToolbar.Title"));
        presentation.setText(message);
        presentation.setEnabledAndVisible(isInstalled && isDraftEditingMode);
    }

    @Override
    protected @NotNull DefaultActionGroup createPopupActionGroup(JComponent component) {

        final var actions = new DefaultActionGroup();

        var project = DataManager.getInstance().getDataContext(component).getData(CommonDataKeys.PROJECT);
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            if (application.isCliInstalled()) {
                var drafts = Try.andHandle(project, application::listDrafts,
                                           AutomateBundle.message("action.DraftsListToolbar.ListAllDrafts.Failure.Message"));
                var isAnyDrafts = drafts != null && !drafts.isEmpty();
                if (isAnyDrafts) {
                    var isNoCurrentDraft = drafts.stream()
                      .noneMatch(DraftLite::getIsCurrent);
                    if (isNoCurrentDraft) {
                        actions.add(new DraftListItemAction(this.onPerformed));
                    }
                    for (var draft : drafts) {
                        actions.add(new DraftListItemAction(this.onPerformed, draft.getName(), draft.getId()));
                    }
                }
            }
        }

        return actions;
    }
}
