package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.intellij.serviceContainer.NonInjectable;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntryType;
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
import jezzsantos.automate.plugin.application.services.interfaces.CliVersionCompatibility;
import jezzsantos.automate.plugin.application.services.interfaces.IApplicationConfiguration;
import jezzsantos.automate.plugin.application.services.interfaces.IAutomateCliService;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AutomateCliService implements IAutomateCliService {

    @NotNull
    private final IAutomationCache cache;
    @NotNull
    private final IApplicationConfiguration configuration;
    @NotNull
    private final IOsPlatform platform;

    @NotNull
    private final IAutomateCliRunner cliRunner;

    @UsedImplicitly
    public AutomateCliService() {

        this(IApplicationConfiguration.getInstance(), new InMemAutomationCache(), new OsPlatform());
    }

    @NonInjectable
    private AutomateCliService(@NotNull IApplicationConfiguration configuration, @NotNull IAutomationCache cache, @NotNull IOsPlatform platform) {

        this(configuration, cache, platform, new AutomateCliRunner());
    }

    @NonInjectable
    public AutomateCliService(@NotNull IApplicationConfiguration configuration, @NotNull IAutomationCache cache, @NotNull IOsPlatform platform, @NotNull IAutomateCliRunner runner) {

        this.configuration = configuration;
        this.cache = cache;
        this.platform = platform;
        this.cliRunner = runner;

        this.configuration.addListener(e -> {

            if (e.getPropertyName().equalsIgnoreCase("ExecutablePath")) {
                var path = (String) e.getNewValue();
                logChangeInExecutablePath(path);
            }
        });

        logChangeInExecutablePath(this.configuration.getExecutablePath());
    }

    @NotNull
    @Override
    public AllStateLite listAllAutomation(@NotNull String currentDirectory, boolean forceRefresh) {

        return this.cache.ListAll(() -> {
            var result = runAutomateForStructuredOutput(ListAllDefinitionsStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("list", "all")));
            if (result.isError()) {
                return new AllStateLite();
            }
            else {
                return result.getOutput().getAll();
            }
        }, forceRefresh);
    }

    @NotNull
    @Override
    public List<PatternLite> listPatterns(@NotNull String currentDirectory) {

        return this.cache.ListPatterns(() -> {
            var result = runAutomateForStructuredOutput(ListPatternsStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("list", "patterns")));
            if (result.isError()) {
                return new ArrayList<>();
            }
            else {
                var patterns = result.getOutput().getPatterns();
                return patterns != null
                  ? patterns
                  : new ArrayList<>();
            }
        });
    }

    @NotNull
    @Override
    public List<ToolkitLite> listToolkits(@NotNull String currentDirectory) {

        return this.cache.ListToolkits(() -> {
            var result = runAutomateForStructuredOutput(ListToolkitsStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("list", "toolkits")));
            if (result.isError()) {
                return new ArrayList<>();
            }
            else {
                var toolkits = result.getOutput().getToolkits();
                return toolkits != null
                  ? toolkits
                  : new ArrayList<>();
            }
        });
    }

    @NotNull
    @Override
    public List<DraftLite> listDrafts(@NotNull String currentDirectory) {

        return this.cache.ListDrafts(() -> {
            var result = runAutomateForStructuredOutput(ListDraftsStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("list", "drafts")));
            if (result.isError()) {
                return new ArrayList<>();
            }
            else {
                var drafts = result.getOutput().getDrafts();
                return drafts != null
                  ? drafts
                  : new ArrayList<>();
            }
        });
    }

    @NotNull
    @Override
    public PatternLite createPattern(@NotNull String currentDirectory, @NotNull String name) throws Exception {

        var result = runAutomateForStructuredOutput(CreatePatternStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("create", "pattern", name)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateAllPatterns();
            return result.getOutput().getPattern();
        }
    }

    @NotNull
    @Override
    public PatternDetailed getCurrentPatternDetailed(@NotNull String currentDirectory) throws Exception {

        return this.cache.GetPatternDetailed(() -> {
            var result = runAutomateForStructuredOutput(GetPatternStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("view", "pattern", "--all")));
            if (result.isError()) {
                throw new Exception(result.getError().getErrorMessage());
            }
            else {
                return result.getOutput().getPattern();
            }
        });
    }

    @Nullable
    @Override
    public PatternLite getCurrentPatternInfo(@NotNull String currentDirectory) {

        return this.cache.GetPatternInfo(() -> {
            var patterns = listPatterns(currentDirectory);

            return patterns.stream()
              .filter(PatternLite::getIsCurrent)
              .findFirst()
              .orElse(null);
        });
    }

    @Override
    public void setCurrentPattern(@NotNull String currentDirectory, @NotNull String id) throws Exception {

        var result = runAutomateForStructuredOutput(SwitchPatternStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("edit", "switch", id)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateAllPatterns();
            result.getOutput().getPattern();
        }
    }

    @NotNull
    @Override
    public DraftDetailed getCurrentDraftDetailed(@NotNull String currentDirectory) throws Exception {

        return this.cache.GetDraftDetailed(() -> {
            var result = runAutomateForStructuredOutput(GetDraftStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("view", "draft")));
            if (result.isError()) {
                throw new Exception(result.getError().getErrorMessage());
            }
            else {
                return result.getOutput().getDraft();
            }
        });
    }

    @NotNull
    @Override
    public ToolkitDetailed getCurrentToolkitDetailed(@NotNull String currentDirectory) throws Exception {

        return this.cache.GetToolkitDetailed(() -> {
            var result = runAutomateForStructuredOutput(GetToolkitStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("view", "toolkit")));
            if (result.isError()) {
                throw new Exception(result.getError().getErrorMessage());
            }
            else {
                return result.getOutput().getToolkit();
            }
        });
    }

    @Nullable
    @Override
    public DraftLite getCurrentDraftInfo(@NotNull String currentDirectory) {

        return this.cache.GetDraftInfo(() -> {
            var drafts = listDrafts(currentDirectory);

            return drafts.stream()
              .filter(DraftLite::getIsCurrent)
              .findFirst()
              .orElse(null);
        });
    }

    @Override
    public void setCurrentDraft(@NotNull String currentDirectory, @NotNull String id) throws Exception {

        var result = runAutomateForStructuredOutput(SwitchDraftStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("run", "switch", id)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateAllDrafts();
            this.cache.invalidateCurrentToolkit();
            result.getOutput().getDraft();
        }
    }

    @NotNull
    @Override
    public DraftLite createDraft(@NotNull String currentDirectory, @NotNull String toolkitName, @NotNull String name) throws Exception {

        var result = runAutomateForStructuredOutput(CreateDraftStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("run", "toolkit", toolkitName, "--name", name)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateAllDrafts();
            return result.getOutput().getDraft();
        }
    }

    @Override
    public void installToolkit(@NotNull String currentDirectory, @NotNull String location) throws Exception {

        var result = runAutomateForStructuredOutput(InstallToolkitStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("install", "toolkit", location)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        this.cache.invalidateAllToolkits();
    }

    @Override
    public void publishCurrentPattern(@NotNull String currentDirectory, boolean installLocally) throws Exception {

        var args = new ArrayList<>(List.of("build", "toolkit"));
        if (installLocally) {
            args.add("--install");
        }
        var result = runAutomateForStructuredOutput(BuildToolkitStructuredOutput.class, currentDirectory, new ArrayList<>(args));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        this.cache.invalidateCurrentPattern();
        this.cache.invalidateAllToolkits();
    }

    @NotNull
    @Override
    public Attribute addPatternAttribute(@NotNull String currentDirectory, @NotNull String parentEditPath, @NotNull String id, boolean isRequired, @NotNull AutomateConstants.AttributeDataType type, @Nullable String defaultValue, @Nullable List<String> choices) throws Exception {

        var args = new ArrayList<>(
          List.of("edit", "add-attribute", id, "--isrequired", Boolean.toString(isRequired), "--isoftype", type.getValue(), "--aschildof", parentEditPath));
        if (defaultValue != null && !defaultValue.isEmpty()) {
            args.addAll(List.of("--defaultvalueis", defaultValue));
        }
        if (choices != null && !choices.isEmpty()) {
            args.addAll(List.of("--isoneof", String.join(";", choices)));
        }
        var result = runAutomateForStructuredOutput(AddRemovePatternAttributeStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
            return result.getOutput().getAttribute();
        }
    }

    @NotNull
    @Override
    public Attribute updatePatternAttribute(@NotNull String currentDirectory, @NotNull String parentEditPath, @NotNull String id, @Nullable String name, boolean isRequired, @NotNull AutomateConstants.AttributeDataType type, @Nullable String defaultValue, @Nullable List<String> choices) throws Exception {

        var args = new ArrayList<>(
          List.of("edit", "update-attribute", id, "--isrequired", Boolean.toString(isRequired), "--isoftype", type.getValue(), "--aschildof", parentEditPath));
        if (name != null) {
            args.addAll(List.of("--name", name));
        }
        if (defaultValue != null && !defaultValue.isEmpty()) {
            args.addAll(List.of("--defaultvalueis", defaultValue));
        }
        if (choices != null && !choices.isEmpty()) {
            args.addAll(List.of("--isoneof", String.join(";", choices)));
        }
        var result = runAutomateForStructuredOutput(AddRemovePatternAttributeStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
            return result.getOutput().getAttribute();
        }
    }

    @Override
    public void deletePatternAttribute(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String name) throws Exception {

        var result = runAutomateForStructuredOutput(AddRemovePatternAttributeStructuredOutput.class, currentDirectory,
                                                    new ArrayList<>(List.of("edit", "delete-attribute", name, "--aschildof", editPath)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
        }
    }

    @NotNull
    @Override
    public DraftElement addDraftElement(@NotNull String currentDirectory, @NotNull String parentConfigurePath, boolean isCollection, @NotNull String elementName, @NotNull Map<String, String> nameValuePairs) throws Exception {

        var configurePath = extendConfigurePath(parentConfigurePath, elementName);
        var args = new ArrayList<>(List.of("configure", isCollection
          ? "add-one-to"
          : "add", configurePath));
        if (!nameValuePairs.isEmpty()) {
            nameValuePairs
              .forEach((key, value) -> {
                  args.add("--and-set");
                  args.add(String.format("%s=%s", key, value));
              });
        }
        var result = runAutomateForStructuredOutput(AddRemoveDraftElementStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentDraft();
            return result.getOutput().getElement();
        }
    }

    @NotNull
    @Override
    public DraftElement updateDraftElement(@NotNull String currentDirectory, @NotNull String configurationPath, @NotNull Map<String, String> nameValuePairs) throws Exception {

        var args = new ArrayList<>(List.of("configure", "on", configurationPath));
        if (!nameValuePairs.isEmpty()) {
            nameValuePairs
              .forEach((key, value) -> {
                  args.add("--and-set");
                  args.add(String.format("%s=%s", key, value));
              });
        }
        var result = runAutomateForStructuredOutput(AddRemoveDraftElementStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentDraft();
            return result.getOutput().getElement();
        }
    }

    @Override
    public void deleteDraftElement(@NotNull String currentDirectory, @NotNull String expression) throws Exception {

        var result = runAutomateForStructuredOutput(AddRemoveDraftElementStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("configure", "delete", expression)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentDraft();
        }
    }

    @Override
    @NotNull
    public LaunchPointExecutionResult executeLaunchPoint(@NotNull String currentDirectory, @NotNull String configurationPath, @NotNull String launchPointName) throws Exception {

        var result = runAutomateForStructuredOutput(ExecuteLaunchPointStructuredOutput.class, currentDirectory,
                                                    new ArrayList<>(List.of("execute", "command", launchPointName, "--on", configurationPath)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            return result.getOutput().getResult();
        }
    }

    @NotNull
    @Override
    public String getExecutableName() {

        return (this.platform.getIsWindowsOs()
          ? String.format("%s.exe", AutomateConstants.ExecutableName)
          : AutomateConstants.ExecutableName);
    }

    @NotNull
    @Override
    public String getDefaultExecutableLocation() {

        return Paths.get(this.platform.getDotNetInstallationDirectory()).resolve(this.getExecutableName()).toString();
    }

    @NotNull
    @Override
    public CliExecutableStatus tryGetExecutableStatus(@NotNull String currentDirectory, @NotNull String executablePath) {

        var location = getExecutablePathSafe(executablePath);
        var file = new File(location);
        var executableName = getExecutableName();

        if (!file.isFile()) {
            return new CliExecutableStatus(executableName);
        }

        if (!file.getName().equalsIgnoreCase(executableName)) {
            return new CliExecutableStatus(executableName);
        }

        var result = this.cliRunner.execute(currentDirectory, location, new ArrayList<>(List.of("--version")));
        if (result.isError()) {
            return new CliExecutableStatus(executableName);
        }

        return new CliExecutableStatus(executableName, result.getOutput());
    }

    @NotNull
    @Override
    public List<CliLogEntry> getCliLog() {

        return this.cliRunner.getLogs();
    }

    @Override
    public boolean isCliInstalled(@NotNull String currentDirectory) {

        return this.cache.isCliInstalled(() -> {
            var executableStatus = tryGetExecutableStatus(currentDirectory, this.configuration.getExecutablePath());
            return executableStatus.getCompatibility() == CliVersionCompatibility.Supported;
        });
    }

    @Override
    public void refreshCliExecutableStatus() {

        this.cache.invalidateIsCliInstalled();
    }

    @Override
    public void addPropertyChangedListener(@NotNull PropertyChangeListener listener) {

        this.cliRunner.addLogListener(listener);
    }

    @Override
    public void removePropertyChangedListener(@NotNull PropertyChangeListener listener) {

        this.cliRunner.removeLogListener(listener);
    }

    @NotNull
    private <TResult extends StructuredOutput<?>> CliStructuredResult<TResult> runAutomateForStructuredOutput(@NotNull Class<TResult> outputClass, @NotNull String currentDirectory, @NotNull List<String> args) {

        if (!isCliInstalled(currentDirectory)) {
            throw new RuntimeException(AutomateBundle.message("exception.AutomateCliService.CliNotInstalled.Message"));
        }

        var executablePath = this.configuration.getExecutablePath();
        return this.cliRunner.executeStructured(outputClass, currentDirectory, getExecutablePathSafe(executablePath), args);
    }

    private String extendConfigurePath(String parentConfigurePath, String elementName) {

        var stringToInsert = String.format(".%s", elementName);
        var insertionIndex = parentConfigurePath.length() - 1;

        var buffer = new StringBuilder(parentConfigurePath);
        buffer.insert(insertionIndex, stringToInsert);
        return buffer.toString();
    }

    @NotNull
    private String getExecutablePathSafe(@NotNull String executablePath) {

        return executablePath.isEmpty()
          ? getDefaultExecutableLocation()
          : executablePath;
    }

    private void logChangeInExecutablePath(String path) {

        var currentDirectory = this.platform.getDotNetInstallationDirectory();
        var executableStatus = tryGetExecutableStatus(currentDirectory, path);
        this.cache.setIsCliInstalled(executableStatus.getCompatibility() == CliVersionCompatibility.Supported);

        String message;
        CliLogEntryType type;
        var executablePath = getExecutablePathSafe(path);
        switch (executableStatus.getCompatibility()) {
            case UnSupported -> {
                message = AutomateBundle.message("general.AutomateCliService.ExecutablePathChanged.UnSupported.Message", executablePath,
                                                 executableStatus.getMinCompatibleVersion());
                type = CliLogEntryType.Error;
            }
            case Supported -> {
                message = AutomateBundle.message("general.AutomateCliService.ExecutablePathChanged.Supported.Message", executablePath);
                type = CliLogEntryType.Normal;
            }
            default -> {
                message = AutomateBundle.message("general.AutomateCliService.ExecutablePathChanged.Unknown.Message", executablePath);
                type = CliLogEntryType.Error;
            }
        }

        var entry = new CliLogEntry(message, type);
        this.cliRunner.log(entry);
    }
}
