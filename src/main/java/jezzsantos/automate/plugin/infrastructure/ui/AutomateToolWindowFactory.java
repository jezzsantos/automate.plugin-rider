package jezzsantos.automate.plugin.infrastructure.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import jezzsantos.automate.plugin.application.AutomateApplication;
import jezzsantos.automate.plugin.infrastructure.common.AutomateService;
import org.jetbrains.annotations.NotNull;

public class AutomateToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        var window = new AutomateToolWindow(project, new AutomateApplication(new AutomateService(project)));
        var contentFactory = ContentFactory.SERVICE.getInstance();
        var content = contentFactory.createContent(window.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }
}
