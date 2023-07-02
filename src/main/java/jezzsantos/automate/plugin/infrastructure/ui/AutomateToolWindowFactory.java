package jezzsantos.automate.plugin.infrastructure.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.services.interfaces.IProjectConfiguration;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.AutomateToolWindow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

@UsedImplicitly
public class AutomateToolWindowFactory implements ToolWindowFactory, Disposable {

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
            var localState = application.warmupAllAutomation();
            if (localState.getPatterns().isEmpty()) {
                if (application.getEditingMode() != EditingMode.DRAFTS) {
                    application.setEditingMode(EditingMode.DRAFTS);
                }
                if (application.isAuthoringMode()) {
                    application.setAuthoringMode(false);
                }
            }
            else {
                if (localState.getToolkits().isEmpty()) {
                    if (application.getEditingMode() != EditingMode.PATTERNS) {
                        application.setEditingMode(EditingMode.PATTERNS);
                    }
                    if (!application.isAuthoringMode()) {
                        application.setAuthoringMode(true);
                    }
                }
            }
        }
    }

    @Override
    public void dispose() {

        IRecorder.getInstance().endOperation(true, "user", AutomateBundle.message("trace.Operation.UserInteraction.End.Message"));
        IRecorder.getInstance().endSession(true, AutomateBundle.message("trace.Operation.Session.End.Message"));
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

        var window = new AutomateToolWindow(project, toolWindow);
        var contentFactory = ContentFactory.getInstance();
        var content = contentFactory.createContent(window.getContent(), "", false);
        toolWindow.getContentManager().addContent(content);
        content.setDisposer(window);
    }

    /**
     * This is essentially the runtime entry point into the plugin
     */
    @Override
    public void init(@NotNull ToolWindow toolWindow) {

        ToolWindowFactory.super.init(toolWindow);
        ensureRecorderIsNotDisposedBeforeThisFactory();

        var project = toolWindow.getProject();
        var allowUsage = IProjectConfiguration.getInstance(project).allowUsageCollection();
        IRecorder.getInstance().startSession(allowUsage, AutomateBundle.message("trace.Operation.Session.Start.Message"));
        initStartupState(project);
    }

    private void ensureRecorderIsNotDisposedBeforeThisFactory() {

        Disposer.register(IRecorder.getInstance(), this);
    }

    private void initStartupState(@NotNull Project project) {

        var application = IAutomateApplication.getInstance(project);
        initStartupState(application);
        IRecorder.getInstance().startOperation("user", AutomateBundle.message("trace.Operation.UserInteraction.Start.Message"));
    }
}
