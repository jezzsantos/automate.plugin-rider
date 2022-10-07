package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.AutomateIcons;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ViewDraftAction extends AnAction {

    private final Runnable onPerformed;
    @Nullable
    private final DraftLite draft;

    public ViewDraftAction(@NotNull Runnable onPerformed) {

        this(onPerformed, null);
    }

    public ViewDraftAction(@NotNull Runnable onPerformed, @Nullable DraftLite draft) {

        super();
        this.onPerformed = onPerformed;
        this.draft = draft;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        super.update(e);
        var message = this.draft == null
          ? ""
          : this.draft.getName();
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        var project = e.getProject();
        DraftLite currentDraft = null;
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            currentDraft = application.getCurrentDraftInfo();
        }
        if (currentDraft == null) {
            presentation.setIcon(null);
        }
        else {
            var isCurrentDraft = currentDraft.getId().equals(this.draft == null
                                                               ? null
                                                               : this.draft.getId());
            var isDraftIncompatible = this.draft != null && this.draft.getVersion().isDraftIncompatible();
            var isDraftToolkitIncompatible = this.draft != null && this.draft.getVersion().isToolkitIncompatible();
            presentation.setIcon(isCurrentDraft
                                   ? AllIcons.Actions.Checked
                                   : isDraftToolkitIncompatible
                                     ? AutomateIcons.StatusError
                                     : isDraftIncompatible
                                       ? AutomateIcons.StatusWarning
                                       : null);
        }
        presentation.setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        if (this.draft != null) {
            var project = e.getProject();
            if (project != null) {
                var application = IAutomateApplication.getInstance(project);
                Try.andHandle(project,
                              () -> application.setCurrentDraft(this.draft.getId()),
                              this.onPerformed,
                              AutomateBundle.message("action.DraftListItem.SetCurrentDraft.Failure.Message"));
            }
        }
    }
}
