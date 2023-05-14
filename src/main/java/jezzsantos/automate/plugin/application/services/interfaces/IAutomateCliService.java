package jezzsantos.automate.plugin.application.services.interfaces;

import com.intellij.openapi.application.ApplicationManager;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.drafts.*;
import jezzsantos.automate.plugin.application.interfaces.patterns.*;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitDetailed;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
import jezzsantos.automate.plugin.common.StringWithDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

public interface IAutomateCliService {

    static IAutomateCliService getInstance() {

        return ApplicationManager.getApplication().getService(IAutomateCliService.class);
    }

    @NotNull
    String getExecutableName();

    @NotNull
    String getDefaultExecutableLocation();

    @NotNull
    CliExecutableStatus tryGetExecutableStatus(@NotNull String currentDirectory, @NotNull StringWithDefault executablePath);

    boolean isCliInstalled(@NotNull String currentDirectory);

    void refreshCliExecutableStatus();

    @NotNull
    List<CliLogEntry> getCliLog();

    void addPropertyChangedListener(@NotNull PropertyChangeListener listener);

    void removePropertyChangedListener(@NotNull PropertyChangeListener listener);

    @NotNull
    AllStateLite listAllAutomation(@NotNull String currentDirectory, boolean forceRefresh);

    @NotNull
    List<PatternLite> listPatterns(@NotNull String currentDirectory);

    @NotNull
    List<ToolkitLite> listToolkits(@NotNull String currentDirectory);

    @NotNull
    List<DraftLite> listDrafts(@NotNull String currentDirectory);

    @Nullable
    ToolkitLite findToolkitById(@NotNull String currentDirectory, @NotNull String id);

    @NotNull
    PatternLite createPattern(@NotNull String currentDirectory, @NotNull String name) throws Exception;

    @NotNull
    PatternElement updatePattern(@NotNull String currentDirectory, @Nullable String name, @Nullable String displayName, @Nullable String description) throws Exception;

    @Nullable
    PatternLite getCurrentPatternInfo(@NotNull String currentDirectory);

    @NotNull
    PatternDetailed getCurrentPatternDetailed(@NotNull String currentDirectory) throws Exception;

    void setCurrentPattern(@NotNull String currentDirectory, @NotNull String id) throws Exception;

    @Nullable
    String publishCurrentPattern(@NotNull String currentDirectory, boolean installLocally, @Nullable String version) throws Exception;

    @NotNull
    Attribute addPatternAttribute(@NotNull String currentDirectory, @NotNull String parentEditPath, @NotNull String id, boolean isRequired, @NotNull AutomateConstants.AttributeDataType type, @Nullable String defaultValue, @Nullable List<String> choices) throws Exception;

    @NotNull
    Attribute updatePatternAttribute(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String id, @Nullable String name, boolean isRequired, @NotNull AutomateConstants.AttributeDataType type, @Nullable String defaultValue, @Nullable List<String> choices) throws Exception;

    void deletePatternAttribute(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String name) throws Exception;

    @NotNull
    PatternElement addPatternElement(@NotNull String currentDirectory, @NotNull String parentEditPath, @NotNull String id, boolean isCollection, boolean isRequired, @Nullable String displayName, @Nullable String description, boolean isAutoCreate) throws Exception;

    @NotNull
    PatternElement updatePatternElement(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String id, @Nullable String name, boolean isCollection, boolean isRequired, @Nullable String displayName, @Nullable String description, boolean isAutoCreate) throws Exception;

    void deletePatternElement(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String name, boolean isCollection) throws Exception;

    @NotNull
    CodeTemplate addPatternCodeTemplate(@NotNull String currentDirectory, @NotNull String parentEditPath, @Nullable String name, @NotNull String filePath) throws Exception;

    @NotNull
    CodeTemplateWithCommand addPatternCodeTemplateWithCommand(@NotNull String currentDirectory, @NotNull String parentEditPath, @Nullable String name, @NotNull String filePath, @Nullable String commandName, @NotNull String targetPath, boolean isOneOff) throws Exception;

    @Nullable
    String getPatternCodeTemplateContent(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String templateName) throws Exception;

    void deletePatternCodeTemplate(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String templateName) throws Exception;

    @NotNull
    Automation addPatternCodeTemplateCommand(@NotNull String currentDirectory, @NotNull String parentEditPath, @Nullable String name, @NotNull String codeTemplateName, @NotNull String targetPath, boolean isOneOff) throws Exception;

    @NotNull
    Automation updatePatternCodeTemplateCommand(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String id, @NotNull String name, @NotNull String targetPath, boolean isOneOff) throws Exception;

    @NotNull
    Automation addPatternCliCommand(@NotNull String currentDirectory, @NotNull String parentEditPath, @Nullable String name, @NotNull String applicationName, @Nullable String arguments) throws Exception;

    @NotNull
    Automation updatePatternCliCommand(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String id, @NotNull String name, @NotNull String applicationName, @Nullable String arguments) throws Exception;

    void deleteCommand(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String commandName) throws Exception;

    @NotNull
    Automation addPatternCommandLaunchPoint(@NotNull String currentDirectory, @NotNull String parentEditPath, @Nullable String name, @NotNull List<String> commandIdentifiers, @Nullable String from) throws Exception;

    @NotNull
    Automation updatePatternCommandLaunchPoint(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String id, @NotNull String name, @NotNull List<String> addIdentifiers, @NotNull List<String> removeIdentifiers, @Nullable String from) throws Exception;

    void deleteLaunchPoint(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String launchPointName) throws Exception;

    void installToolkit(@NotNull String currentDirectory, @NotNull String location) throws Exception;

    @NotNull
    ToolkitDetailed getCurrentToolkitDetailed(@NotNull String currentDirectory) throws Exception;

    @NotNull
    DraftLite createDraft(@NotNull String currentDirectory, @NotNull String toolkitName, @NotNull String name) throws Exception;

    @Nullable
    DraftLite getCurrentDraftInfo(@NotNull String currentDirectory);

    @NotNull
    DraftDetailed getCurrentDraftDetailed(@NotNull String currentDirectory) throws Exception;

    void setCurrentDraft(@NotNull String currentDirectory, @NotNull String id) throws Exception;

    @NotNull
    DraftElement addDraftElement(@NotNull String currentDirectory, @NotNull String parentConfigurePath, boolean isCollection, @NotNull String elementName, @NotNull Map<String, String> nameValuePairs) throws Exception;

    @NotNull
    DraftElement updateDraftElement(@NotNull String currentDirectory, @NotNull String configurationPath, @NotNull Map<String, String> nameValuePairs) throws Exception;

    void deleteDraftElement(@NotNull String currentDirectory, @NotNull String expression) throws Exception;

    @NotNull
    DraftUpgradeReport upgradeCurrentDraft(@NotNull String currentDirectory, boolean force) throws Exception;

    void deleteCurrentDraft(String currentDirectory) throws Exception;

    @NotNull
    LaunchPointExecutionResult executeLaunchPoint(@NotNull String currentDirectory, @NotNull String configurationPath, @NotNull String launchPointName) throws Exception;
}

