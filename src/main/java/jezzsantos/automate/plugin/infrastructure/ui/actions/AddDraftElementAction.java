package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.common.Action;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.DraftTreeModel;
import org.jetbrains.annotations.NotNull;

public class AddDraftElementAction extends AnAction {

    private final Action<DraftTreeModel> onSuccess;
    private final PatternElement patternElement;

    public AddDraftElementAction(@NotNull PatternElement element, Action<DraftTreeModel> onSuccess) {

        super();
        this.patternElement = element;
        this.onSuccess = onSuccess;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {

        var message = AutomateBundle.message("action.AddDraftElement.Title", this.patternElement.getDisplayName());
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setEnabledAndVisible(true);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

    }
}
