package jezzsantos.automate.plugin.application;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.drafts.*;
import jezzsantos.automate.plugin.application.interfaces.patterns.*;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitDetailed;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
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

    @Nullable
    ToolkitLite findToolkitById(@NotNull String id);

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
    AllStateLite warmupAllAutomation();

    @NotNull
    AllStateLite listAllAutomation(boolean forceRefresh);

    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    PatternLite createPattern(@NotNull String name) throws Exception;

    @NotNull
    PatternElement updatePattern(@Nullable String name, @Nullable String displayName, @Nullable String description) throws Exception;

    @NotNull
    Attribute addPatternAttribute(@NotNull String parentEditPath, @NotNull String id, boolean isRequired, @NotNull AutomateConstants.AttributeDataType type, @Nullable String defaultValue, @Nullable List<String> choices) throws Exception;

    @NotNull
    Attribute updatePatternAttribute(@NotNull String editPath, @NotNull String id, @Nullable String name, boolean isRequired, @NotNull AutomateConstants.AttributeDataType type, @Nullable String defaultValue, @Nullable List<String> choices) throws Exception;

    void deletePatternAttribute(@NotNull String editPath, @NotNull String name) throws Exception;

    @NotNull
    PatternElement addPatternElement(@NotNull String parentEditPath, @NotNull String id, boolean isCollection, boolean isRequired, @Nullable String displayName, @Nullable String description, boolean isAutoCreate) throws Exception;

    @NotNull
    PatternElement updatePatternElement(@NotNull String editPath, @NotNull String id, @Nullable String name, boolean isCollection, boolean isRequired, @Nullable String displayName, @Nullable String description, boolean isAutoCreate) throws Exception;

    void deletePatternElement(@NotNull String editPath, @NotNull String name, boolean isCollection) throws Exception;

    @NotNull
    CodeTemplate addPatternCodeTemplate(@NotNull String parentEditPath, @Nullable String name, @NotNull String filePath) throws Exception;

    @NotNull
    CodeTemplateWithCommand addPatternCodeTemplateWithCommand(@NotNull String parentEditPath, @Nullable String name, @NotNull String filePath, @Nullable String commandName, @NotNull String targetPath, boolean isOneOff) throws Exception;

    @Nullable
    String getPatternCodeTemplateContent(@NotNull String editPath, @NotNull String templateName) throws Exception;

    void deletePatternCodeTemplate(@NotNull String editPath, @NotNull String templateName) throws Exception;

    @NotNull
    Automation addPatternCodeTemplateCommand(@NotNull String parentEditPath, @Nullable String name, @NotNull String codeTemplateName, @NotNull String targetPath, boolean isOneOff) throws Exception;

    @NotNull
    Automation updatePatternCodeTemplateCommand(@NotNull String editPath, @NotNull String id, @NotNull String name, @NotNull String targetPath, boolean isOneOff) throws Exception;

    @NotNull
    Automation addPatternCliCommand(@NotNull String parentEditPath, @Nullable String name, @NotNull String applicationName, @Nullable String arguments) throws Exception;

    @NotNull
    Automation updatePatternCliCommand(@NotNull String editPath, @NotNull String id, @NotNull String name, @NotNull String applicationName, @Nullable String arguments) throws Exception;

    void deleteCommand(@NotNull String editPath, @NotNull String commandName) throws Exception;

    @NotNull
    Automation addPatternCommandLaunchPoint(@NotNull String parentEditPath, @Nullable String name, @NotNull List<String> commandIdentifiers, @Nullable String from) throws Exception;

    @NotNull
    Automation updatePatternCommandLaunchPoint(@NotNull String editPath, @NotNull String id, @NotNull String name, @NotNull List<String> addIdentifiers, @NotNull List<String> removeIdentifiers, @Nullable String from) throws Exception;

    void deleteLaunchPoint(@NotNull String editPath, @NotNull String launchPointName) throws Exception;

    @Nullable
    String publishCurrentPattern(boolean installLocally, @Nullable String version) throws Exception;

    @NotNull
    DraftElement addDraftElement(@NotNull String parentConfigurePath, boolean isCollection, @NotNull String elementName, @NotNull Map<String, String> nameValuePairs) throws Exception;

    @NotNull
    DraftElement updateDraftElement(@NotNull String configurationPath, @NotNull Map<String, String> nameValuePairs) throws Exception;

    void deleteDraftElement(@NotNull String expression) throws Exception;

    @NotNull
    DraftUpgradeReport upgradeCurrentDraft(boolean force) throws Exception;

    void deleteCurrentDraft() throws Exception;

    @NotNull
    LaunchPointExecutionResult executeLaunchPoint(@NotNull String configurationPath, @NotNull String launchPointName) throws Exception;
}
