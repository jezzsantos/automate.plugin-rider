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
    AllDefinitions listAllAutomation(boolean forceRefresh);

    @NotNull
    List<PatternDefinition> listPatterns();

    @NotNull
    List<ToolkitDefinition> listToolkits();

    @NotNull
    List<DraftDefinition> listDrafts();

    @NotNull
    PatternDefinition createPattern(@NotNull String name) throws Exception;

    @Nullable
    PatternDefinition getCurrentPattern();

    void setCurrentPattern(@NotNull String id) throws Exception;

    @Nullable
    DraftDefinition getCurrentDraft();

    void setCurrentDraft(@NotNull String id) throws Exception;

    @NotNull
    DraftDefinition createDraft(@NotNull String toolkitName, @NotNull String name) throws Exception;

    void installToolkit(@NotNull String location) throws Exception;

    void addPropertyChangedListener(@NotNull PropertyChangeListener listener);

    void removePropertyChangedListener(@NotNull PropertyChangeListener listener);

    @NotNull
    List<CliLogEntry> getCliLog();
}
