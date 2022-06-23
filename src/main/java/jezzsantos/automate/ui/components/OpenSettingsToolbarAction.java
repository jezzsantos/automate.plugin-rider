package jezzsantos.automate.ui.components;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import jezzsantos.automate.AutomateBundle;
import jezzsantos.automate.settings.ProjectSettingsConfigurable;
import org.jetbrains.annotations.NotNull;

public class OpenSettingsToolbarAction extends AnAction {
    public OpenSettingsToolbarAction() {
        super();

        String message = AutomateBundle.message("action.Automate.ShowSettings");
        this.getTemplatePresentation().setText(message);
        this.getTemplatePresentation().setIcon(AllIcons.General.Gear);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(CommonDataKeys.PROJECT);
        if (project != null)
            ShowSettingsUtil.getInstance().showSettingsDialog(project, ProjectSettingsConfigurable.class);
    }
}
