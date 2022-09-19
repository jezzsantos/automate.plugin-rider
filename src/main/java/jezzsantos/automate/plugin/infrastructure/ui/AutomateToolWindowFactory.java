package jezzsantos.automate.plugin.infrastructure.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.AutomateToolWindow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

public class AutomateToolWindowFactory implements ToolWindowFactory {

    /**
     * Resets initial state of the tools given the current state of existing configuration
     * (EditingMode, AuthoringMode) AND the local state of automate.
     * AnyPatterns?  AnyToolkits?
     * None          N/A         EditingMode=Drafts, AuthoringMode=OFF
     * Some          None        EditingMode=Patterns, AuthoringMode=ON
     * Some          Some        EditingMode=Existing, AuthoringMode=Existing
     */
    @SuppressWarnings("GrazieInspection")
    @TestOnly
    public static void initStartupState(@NotNull IAutomateApplication application) {

        if (application.isCliInstalled()) {
            var localState = application.listAllAutomation(false);
            if (localState.getPatterns().isEmpty()) {
                if (application.getEditingMode() != EditingMode.Drafts) {
                    application.setEditingMode(EditingMode.Drafts);
                }
                if (application.isAuthoringMode()) {
                    application.setAuthoringMode(false);
                }
            }
            else {
                if (localState.getToolkits().isEmpty()) {
                    if (application.getEditingMode() != EditingMode.Patterns) {
                        application.setEditingMode(EditingMode.Patterns);
                    }
                    if (!application.isAuthoringMode()) {
                        application.setAuthoringMode(true);
                    }
                }
            }
        }
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {

        ToolWindowFactory.super.init(toolWindow);
        var project = toolWindow.getProject();
        initStartupState(project);
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        var window = new AutomateToolWindow(project, toolWindow);
        var contentFactory = ContentFactory.getInstance();
        var content = contentFactory.createContent(window.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
        content.setDisposer(window);
    }

    private void initStartupState(@NotNull Project project) {

        var application = IAutomateApplication.getInstance(project);
        initStartupState(application);
    }
}
