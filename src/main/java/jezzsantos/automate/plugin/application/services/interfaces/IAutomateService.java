package jezzsantos.automate.plugin.application.services.interfaces;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftDetailed;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternDetailed;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitDetailed;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
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
    String getDefaultExecutableLocation();

    @NotNull
    CliExecutableStatus tryGetExecutableStatus(@NotNull String executablePath);

    boolean isCliInstalled();

    void refreshCliExecutableStatus();

    @NotNull
    List<CliLogEntry> getCliLog();

    void addPropertyChangedListener(@NotNull PropertyChangeListener listener);

    void removePropertyChangedListener(@NotNull PropertyChangeListener listener);

    @NotNull
    AllStateLite listAllAutomation(boolean forceRefresh);

    @NotNull
    List<PatternLite> listPatterns();

    @NotNull
    List<ToolkitLite> listToolkits();

    @NotNull
    List<DraftLite> listDrafts();

    @NotNull
    PatternLite createPattern(@NotNull String name) throws Exception;

    @NotNull
    PatternDetailed getCurrentPatternDetailed() throws Exception;

    @Nullable
    PatternLite getCurrentPatternInfo();

    void setCurrentPattern(@NotNull String id) throws Exception;

    @NotNull
    DraftDetailed getCurrentDraftDetailed() throws Exception;

    @NotNull
    ToolkitDetailed getCurrentToolkitDetailed() throws Exception;

    @Nullable
    DraftLite getCurrentDraftInfo();

    void setCurrentDraft(@NotNull String id) throws Exception;

    @NotNull
    DraftLite createDraft(@NotNull String toolkitName, @NotNull String name) throws Exception;

    void installToolkit(@NotNull String location) throws Exception;

    void publishCurrentPattern(boolean installLocally) throws Exception;

    Attribute addPatternAttribute(@NotNull String editPath, @NotNull String name, boolean isRequired, @NotNull String type, @Nullable String defaultValue, @Nullable List<String> choices) throws Exception;

    void deletePatternAttribute(@NotNull String editPath, @NotNull String name) throws Exception;

    void deleteDraftElement(@NotNull String expression) throws Exception;
}

