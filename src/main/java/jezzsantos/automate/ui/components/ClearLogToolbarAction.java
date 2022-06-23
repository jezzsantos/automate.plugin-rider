package jezzsantos.automate.ui.components;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import jezzsantos.automate.AutomateBundle;

public abstract class ClearLogToolbarAction extends AnAction {
    public ClearLogToolbarAction() {
        String message = AutomateBundle.message("action.Automate.ClearLogs.text");
        this.getTemplatePresentation().setDescription(message);
        this.getTemplatePresentation().setText(message);
        this.getTemplatePresentation().setIcon(AllIcons.Actions.GC);
    }

    @Override
    public boolean isDumbAware() {
        return false;
    }
}
