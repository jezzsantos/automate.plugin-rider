package jezzsantos.automate.plugin.application.services.interfaces;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.interfaces.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface IAutomateService {

    static IAutomateService getInstance(Project project) {
        return project.getService(IAutomateService.class);
    }

    @NotNull
    String getExecutableName();

    @NotNull
    String getDefaultInstallLocation();

    @Nullable
    String tryGetExecutableVersion(@NotNull String executablePath);

    @NotNull
    AllDefinitions listAllAutomation(@NotNull String executablePath, boolean forceRefresh);

    @NotNull
    List<PatternDefinition> listPatterns(@NotNull String executablePath);

    @NotNull
    List<ToolkitDefinition> listToolkits(@NotNull String executablePath);

    @NotNull
    List<DraftDefinition> listDrafts(@NotNull String executablePath);

    @NotNull
    PatternDefinition createPattern(@NotNull String executablePath, @NotNull String name) throws Exception;

    void setCurrentPattern(@NotNull String executablePath, @NotNull String id) throws Exception;

    @Nullable
    PatternDefinition getCurrentPattern(@NotNull String executablePath);

    @Nullable
    DraftDefinition getCurrentDraft(@NotNull String executablePath);

    void setCurrentDraft(@NotNull String executablePath, @NotNull String id) throws Exception;

    @NotNull
    DraftDefinition createDraft(@NotNull String executablePath, @NotNull String toolkitName, @NotNull String name) throws Exception;

    void installToolkit(@NotNull String executablePath, @NotNull String location) throws Exception;

    void addCliLogListener(@NotNull PropertyChangeListener listener);

    void removeCliLogListener(@NotNull PropertyChangeListener listener);

    @NotNull
    List<CliLogEntry> getCliLog();
}
