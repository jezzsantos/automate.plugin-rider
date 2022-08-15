package jezzsantos.automate.plugin.infrastructure.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.AutomateToolWindow;
import org.jetbrains.annotations.NotNull;

public class AutomateToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        initStartupState(project);
        var window = new AutomateToolWindow(project);
        var contentFactory = ContentFactory.SERVICE.getInstance();
        var content = contentFactory.createContent(window.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    private void initStartupState(@NotNull Project project) {
        var application = IAutomateApplication.getInstance(project);
        var automation = application.getAllAutomation(false);
        if (automation.getPatterns().isEmpty()) {
            if (application.getEditingMode() != EditingMode.Drafts) {
                application.setEditingMode(EditingMode.Drafts);
            }
            if (automation.getToolkits().isEmpty()) {
                if (automation.getDrafts().isEmpty()) {
                    if (application.isAuthoringMode()) {
                        application.setAuthoringMode(false);
                    }
                }
            }
        } else {
            if (automation.getToolkits().isEmpty()) {
                if (application.getEditingMode() != EditingMode.Patterns) {
                    application.setEditingMode(EditingMode.Patterns);
                }
            }
        }

        if (application.getEditingMode() == EditingMode.Patterns) {
            if (!application.isAuthoringMode()) {
                application.setAuthoringMode(true);
            }
        }
    }
}
