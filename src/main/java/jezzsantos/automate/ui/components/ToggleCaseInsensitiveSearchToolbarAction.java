package jezzsantos.automate.ui.components;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import jezzsantos.automate.AutomateBundle;
import jezzsantos.automate.settings.AppSettingState;
import org.jetbrains.annotations.NotNull;

public class ToggleCaseInsensitiveSearchToolbarAction extends ToggleAction {

    public ToggleCaseInsensitiveSearchToolbarAction() {
        super();

        String message = AutomateBundle.message("action.Automate.ToggleCaseInsensitive.text");
        this.getTemplatePresentation().setDescription(message);
        this.getTemplatePresentation().setText(message);
        this.getTemplatePresentation().setIcon(AllIcons.Actions.MatchCase);
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent actionEvent) {
        return AppSettingState.getInstance().caseInsensitiveSearch.getValue();
    }

    public void setSelected(@NotNull AnActionEvent actionEvent, boolean state) {
        AppSettingState.getInstance().caseInsensitiveSearch.setValue(state);
    }
}
