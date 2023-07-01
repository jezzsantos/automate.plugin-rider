package jezzsantos.automate.plugin.infrastructure.ui.actions.drafts;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElement;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.dialogs.drafts.EditDraftElementDialog;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.DraftTreeModel;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AddDraftElementAction extends AnAction {

    private final Action<DraftTreeModel> onSuccess;
    private final DraftElement parentElement;
    private final PatternElement schema;

    public AddDraftElementAction(@NotNull DraftElement parentElement, @NotNull PatternElement schema, Action<DraftTreeModel> onSuccess) {

        super();
        this.parentElement = parentElement;
        this.schema = schema;
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        var message = AutomateBundle.message("action.AddDraftElement.Title", this.schema.getDisplayName());
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        IRecorder.getInstance().measureEvent("action.draft.element.add", null);

        var project = e.getProject();
        if (project != null) {
            var application = IAutomateApplication.getInstance(project);
            var dialog = new EditDraftElementDialog(project, new EditDraftElementDialog.EditDraftElementDialogContext(this.schema));
            if (dialog.showAndGet()) {
                var context = dialog.getContext();
                var added = Try.andHandle(project, AutomateBundle.message("action.AddDraftElement.NewElement.Progress.Title"),
                                          () -> application.addDraftElement(Objects.requireNonNull(this.parentElement.getConfigurePath()), this.schema.isCollection(),
                                                                            this.schema.getName(),
                                                                            context.getValues()),
                                          AutomateBundle.message("action.AddDraftElement.NewElement.Failure.Message"));
                if (added != null) {
                    this.onSuccess.run(model -> model.insertDraftElement(added, this.schema.isCollection()));
                }
            }
        }
    }
}
