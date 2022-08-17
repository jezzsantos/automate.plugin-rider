package jezzsantos.automate.plugin.application;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.interfaces.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface IAutomateApplication {

    static IAutomateApplication getInstance(Project project) {
        return project.getService(IAutomateApplication.class);
    }

    @NotNull
    String getExecutableName();

    @NotNull
    String getDefaultInstallLocation();

    @Nullable
    String tryGetExecutableVersion(@NotNull String executablePath);

    @NotNull
    List<PatternDefinition> getPatterns();

    @NotNull
    List<ToolkitDefinition> getToolkits();

    @NotNull
    List<DraftDefinition> getDrafts();

    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    PatternDefinition createPattern(@NotNull String name) throws Exception;

    boolean isAuthoringMode();

    void setAuthoringMode(boolean on);

    EditingMode getEditingMode();

    void setEditingMode(@NotNull EditingMode mode);

    @Nullable
    PatternDefinition getCurrentPattern();

    void setCurrentPattern(@NotNull String id) throws Exception;

    @Nullable
    DraftDefinition getCurrentDraft();

    void setCurrentDraft(@NotNull String id) throws Exception;

    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    DraftDefinition createDraft(@NotNull String toolkitName, @NotNull String name) throws Exception;

    void installToolkit(@NotNull String location) throws Exception;

    @NotNull
    AllDefinitions getAllAutomation();

    @NotNull
    AllDefinitions getAllAutomation(boolean forceRefresh);

    boolean getViewCliLog();

    void addCliLogListener(@NotNull PropertyChangeListener listener);

    void removeCliLogListener(@NotNull PropertyChangeListener listener);

    @NotNull
    List<CliLogEntry> getCliLog();
}
