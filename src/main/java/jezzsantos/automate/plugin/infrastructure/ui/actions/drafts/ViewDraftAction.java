package jezzsantos.automate.plugin.infrastructure.ui.actions.drafts;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.AutomateIcons;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
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
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            var currentDraft = Try.andHandleWithoutProgress(project, AutomateBundle.message("action.ViewDraft.GetCurrentDraft.Progress.Title"),
                                                            application::getCurrentDraftInfo,
                                                            AutomateBundle.message("action.ViewDraft.GetCurrentDraft.Failure.Message"));
            var isThisDraftCurrentDraft = currentDraft != null && currentDraft.getId().equals(this.draft == null
                                                                                                ? null
                                                                                                : this.draft.getId());
            var isThisDraftIncompatible = this.draft != null && this.draft.getVersion().isDraftIncompatible();
            var isThisDraftToolkitIncompatible = this.draft != null && this.draft.getVersion().isRuntimeIncompatible();
            presentation.setIcon(isThisDraftCurrentDraft
                                   ? AllIcons.Actions.Checked
                                   : isThisDraftToolkitIncompatible
                                     ? AutomateIcons.StatusError
                                     : isThisDraftIncompatible
                                       ? AutomateIcons.StatusWarning
                                       : null);
        }
        presentation.setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.draft.view", null);

        if (this.draft != null) {
            var project = e.getProject();
            if (project != null) {
                var application = IAutomateApplication.getInstance(project);
                Try.andHandle(project, AutomateBundle.message("action.ViewDraft.SetCurrentDraft.Progress.Title"),
                              () -> application.setCurrentDraft(this.draft.getId()),
                              this.onPerformed,
                              AutomateBundle.message("action.ViewDraft.SetCurrentDraft.Failure.Message"));
            }
        }
    }
}
