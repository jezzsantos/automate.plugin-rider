package jezzsantos.automate.plugin.application;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftDetailed;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElement;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
import jezzsantos.automate.plugin.application.interfaces.drafts.LaunchPointExecutionResult;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternDetailed;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitDetailed;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
import jezzsantos.automate.plugin.application.services.interfaces.CliExecutableStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

public interface IAutomateApplication {

    static IAutomateApplication getInstance(Project project) {

        return project.getService(IAutomateApplication.class);
    }

    @NotNull
    String getExecutableName();

    @NotNull
    String getDefaultExecutableLocation();

    @NotNull
    CliExecutableStatus tryGetExecutableStatus(@NotNull String executablePath);

    boolean isCliInstalled();

    void addPropertyListener(@NotNull PropertyChangeListener listener);

    void removePropertyListener(@NotNull PropertyChangeListener listener);

    void addConfigurationListener(@NotNull PropertyChangeListener listener);

    void removeConfigurationListener(@NotNull PropertyChangeListener listener);

    @NotNull
    List<CliLogEntry> getCliLogEntries();

    boolean getViewCliLog();

    @NotNull
    List<PatternLite> listPatterns();

    @NotNull
    List<ToolkitLite> listToolkits();

    @NotNull
    List<DraftLite> listDrafts();

    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    PatternLite createPattern(@NotNull String name) throws Exception;

    boolean isAuthoringMode();

    void setAuthoringMode(boolean on);

    EditingMode getEditingMode();

    void setEditingMode(@NotNull EditingMode mode);

    @NotNull
    PatternDetailed getCurrentPatternDetailed() throws Exception;

    @Nullable
    PatternLite getCurrentPatternInfo();

    void setCurrentPattern(@NotNull String id) throws Exception;

    @NotNull
    ToolkitDetailed getCurrentToolkitDetailed() throws Exception;

    @NotNull
    DraftDetailed getCurrentDraftDetailed() throws Exception;

    @Nullable
    DraftLite getCurrentDraftInfo();

    void setCurrentDraft(@NotNull String id) throws Exception;

    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    DraftLite createDraft(@NotNull String toolkitName, @NotNull String name) throws Exception;

    void installToolkit(@NotNull String location) throws Exception;

    @NotNull
    AllStateLite listAllAutomation(boolean forceRefresh);

    @NotNull
    Attribute addPatternAttribute(@NotNull String parentEditPath, @NotNull String id, boolean isRequired, @NotNull AutomateConstants.AttributeDataType type, @Nullable String defaultValue, @Nullable List<String> choices) throws Exception;

    @NotNull
    Attribute updatePatternAttribute(@NotNull String parentEditPath, @NotNull String id, @Nullable String name, boolean isRequired, @NotNull AutomateConstants.AttributeDataType type, @Nullable String defaultValue, @Nullable List<String> choices) throws Exception;

    void deletePatternAttribute(@NotNull String editPath, @NotNull String name) throws Exception;

    @NotNull
    DraftElement addDraftElement(@NotNull String parentConfigurePath, boolean isCollection, @NotNull String elementName, @NotNull Map<String, String> nameValuePairs) throws Exception;

    @NotNull
    DraftElement updateDraftElement(@NotNull String configurationPath, @NotNull Map<String, String> nameValuePairs) throws Exception;

    void deleteDraftElement(@NotNull String expression) throws Exception;

    @NotNull
    LaunchPointExecutionResult executeLaunchPoint(@NotNull String configurationPath, @NotNull String launchPointName) throws Exception;
}
