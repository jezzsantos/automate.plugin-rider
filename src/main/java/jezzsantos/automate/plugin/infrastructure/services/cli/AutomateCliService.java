package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.intellij.serviceContainer.NonInjectable;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntryType;
import jezzsantos.automate.plugin.application.interfaces.drafts.*;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternDetailed;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitDetailed;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
import jezzsantos.automate.plugin.application.services.interfaces.CliExecutableStatus;
import jezzsantos.automate.plugin.application.services.interfaces.CliVersionCompatibility;
import jezzsantos.automate.plugin.application.services.interfaces.IApplicationConfiguration;
import jezzsantos.automate.plugin.application.services.interfaces.IAutomateCliService;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.StringWithDefault;
import jezzsantos.automate.plugin.infrastructure.IOsPlatform;
import jezzsantos.automate.plugin.infrastructure.OsPlatform;
import jezzsantos.automate.plugin.infrastructure.ui.IntelliJNotifier;
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
    private final ICliResponseCache cache;
    @NotNull
    private final IApplicationConfiguration configuration;
    @NotNull
    private final IOsPlatform platform;

    @NotNull
    private final IAutomateCliRunner cliRunner;
    private final ICliUpgrader upgrader;

    @UsedImplicitly
    public AutomateCliService() {

        this(IApplicationConfiguration.getInstance(), new InMemCliResponseCache(), new OsPlatform());
    }

    @NonInjectable
    private AutomateCliService(@NotNull IApplicationConfiguration configuration, @NotNull ICliResponseCache cache, @NotNull IOsPlatform platform) {

        this(configuration, cache, platform, new AutomateCliRunner());
    }

    @NonInjectable
    private AutomateCliService(@NotNull IApplicationConfiguration configuration, @NotNull ICliResponseCache cache, @NotNull IOsPlatform platform, @NotNull IAutomateCliRunner runner) {

        this(configuration, cache, platform, runner, new AutomateCliUpgrader(runner, new IntelliJNotifier()));
    }

    @NonInjectable
    public AutomateCliService(@NotNull IApplicationConfiguration configuration, @NotNull ICliResponseCache cache, @NotNull IOsPlatform platform, @NotNull IAutomateCliRunner runner, @NotNull ICliUpgrader upgrader) {

        this.configuration = configuration;
        this.cache = cache;
        this.platform = platform;
        this.cliRunner = runner;
        this.upgrader = upgrader;

        this.configuration.addListener(e -> {

            if (e.getPropertyName().equalsIgnoreCase("ExecutablePath")) {
                var path = (StringWithDefault) e.getNewValue();
                logChangeInExecutablePath(path);
            }
        });

        init();
    }

    @NotNull
    public static String getExecutableName(@NotNull IOsPlatform platform) {

        return (platform.getIsWindowsOs()
          ? String.format("%s.exe", AutomateConstants.ExecutableName)
          : AutomateConstants.ExecutableName);
    }

    @NotNull
    public static String getDefaultExecutableLocation(@NotNull IOsPlatform platform) {

        return Paths.get(platform.getDotNetInstallationDirectory()).resolve(getExecutableName(platform)).toString();
    }

    @NotNull
    @Override
    public String getExecutableName() {

        return getExecutableName(this.platform);
    }

    @NotNull
    @Override
    public String getDefaultExecutableLocation() {

        return getDefaultExecutableLocation(this.platform);
    }

    @NotNull
    @Override
    public CliExecutableStatus tryGetExecutableStatus(@NotNull String currentDirectory, @NotNull StringWithDefault executablePath) {

        var location = executablePath.getActualValue();
        var file = new File(location);
        var executableName = getExecutableName();

        if (!file.isFile()) {
            return new CliExecutableStatus(executableName);
        }

        if (!file.getName().equalsIgnoreCase(executableName)) {
            return new CliExecutableStatus(executableName);
        }

        var result = this.cliRunner.execute(currentDirectory, executablePath, new ArrayList<>(List.of("--version")));
        if (result.isError()) {
            return new CliExecutableStatus(executableName);
        }

        return new CliExecutableStatus(executableName, result.getOutput());
    }

    @Override
    public boolean isCliInstalled(@NotNull String currentDirectory) {

        return this.cache.isCliInstalled(() -> {
            var executableStatus = tryGetExecutableStatus(currentDirectory, this.configuration.getExecutablePath());
            return executableStatus.getCompatibility() == CliVersionCompatibility.COMPATIBLE;
        });
    }

    @Override
    public void refreshCliExecutableStatus() {

        this.cache.invalidateIsCliInstalled();
    }

    @NotNull
    @Override
    public List<CliLogEntry> getCliLog() {

        return this.cliRunner.getLogs();
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
    @Override
    public AllStateLite listAllAutomation(@NotNull String currentDirectory, boolean forceRefresh) {

        return this.cache.listAll(() -> {
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

        return this.cache.listPatterns(() -> {
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

        return this.cache.listToolkits(() -> {
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

        return this.cache.listDrafts(() -> {
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

    @Nullable
    @Override
    public PatternLite getCurrentPatternInfo(@NotNull String currentDirectory) {

        return this.cache.getPatternInfo(() -> {
            var patterns = listPatterns(currentDirectory);

            return patterns.stream()
              .filter(PatternLite::getIsCurrent)
              .findFirst()
              .orElse(null);
        });
    }

    @NotNull
    @Override
    public PatternDetailed getCurrentPatternDetailed(@NotNull String currentDirectory) throws Exception {

        return this.cache.getPatternDetailed(() -> {
            var result = runAutomateForStructuredOutput(GetPatternStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("view", "pattern", "--all")));
            if (result.isError()) {
                throw new Exception(result.getError().getErrorMessage());
            }
            else {
                return result.getOutput().getPattern();
            }
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

    @Override
    public void installToolkit(@NotNull String currentDirectory, @NotNull String location) throws Exception {

        var result = runAutomateForStructuredOutput(InstallToolkitStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("install", "toolkit", location)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        this.cache.invalidateAllToolkits();
    }

    @NotNull
    @Override
    public ToolkitDetailed getCurrentToolkitDetailed(@NotNull String currentDirectory) throws Exception {

        return this.cache.getToolkitDetailed(() -> {
            var result = runAutomateForStructuredOutput(GetToolkitStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("view", "toolkit")));
            if (result.isError()) {
                throw new Exception(result.getError().getErrorMessage());
            }
            else {
                return result.getOutput().getToolkit();
            }
        });
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

    @Nullable
    @Override
    public DraftLite getCurrentDraftInfo(@NotNull String currentDirectory) {

        return this.cache.getDraftInfo(() -> {
            var drafts = listDrafts(currentDirectory);

            return drafts.stream()
              .filter(DraftLite::getIsCurrent)
              .findFirst()
              .orElse(null);
        });
    }

    @NotNull
    @Override
    public DraftDetailed getCurrentDraftDetailed(@NotNull String currentDirectory) throws Exception {

        var outOfDateDraft = getOutOfDateSafely(currentDirectory);
        if (outOfDateDraft != null) {
            this.cache.setDraftDetailed(outOfDateDraft);
            return outOfDateDraft;
        }

        return this.cache.getDraftDetailed(() -> {
            var result = runAutomateForStructuredOutput(GetDraftStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("view", "draft")));
            if (result.isError()) {
                throw new Exception(result.getError().getErrorMessage());
            }
            else {
                return result.getOutput().getDraft();
            }
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

    @NotNull
    @Override
    public DraftUpgradeReport upgradeDraft(@NotNull String currentDirectory, boolean force) throws Exception {

        var args = new ArrayList<>(List.of("upgrade", "draft"));
        if (force) {
            args.add("--force");
        }
        var result = runAutomateForStructuredOutput(UpgradeDraftStructuredOutput.class, currentDirectory, new ArrayList<>(args));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentDraft();
            return result.getOutput().getReport();
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

    private DraftDetailed getOutOfDateSafely(@NotNull String currentDirectory) {

        var draftInfo = getCurrentDraftInfo(currentDirectory);
        if (draftInfo != null) {
            if (draftInfo.isOutOfDate()) {
                return DraftDetailed.createMustUpgrade(draftInfo.getId(), draftInfo.getName(), draftInfo.getOriginalToolkitVersion(), draftInfo.getCurrentToolkitVersion());
            }
        }

        return null;
    }

    private void init() {

        var executablePath = this.configuration.getExecutablePath();
        var executableStatus = refreshExecutableStatus(executablePath);
        var installPolicy = this.configuration.getCliInstallPolicy();
        var executableName = this.getExecutableName();

        if (executableStatus.getCompatibility() != CliVersionCompatibility.COMPATIBLE) {
            var currentDirectory = this.platform.getDotNetInstallationDirectory();
            executableStatus = this.upgrader.upgrade(currentDirectory, executablePath, executableName, executableStatus, installPolicy);
            saveStatusIfSupported(executableStatus);
        }

        logChangeInExecutablePath(executableStatus, executablePath);
    }

    @NotNull
    private <TResult extends StructuredOutput<?>> CliStructuredResult<TResult> runAutomateForStructuredOutput(@NotNull Class<TResult> outputClass, @NotNull String currentDirectory, @NotNull List<String> args) {

        if (!isCliInstalled(currentDirectory)) {
            throw new RuntimeException(AutomateBundle.message("exception.AutomateCliService.CliNotInstalled.Message"));
        }

        var executablePath = this.configuration.getExecutablePath();
        return this.cliRunner.executeStructured(outputClass, currentDirectory, executablePath, args);
    }

    @NotNull
    private String extendConfigurePath(@NotNull String parentConfigurePath, String elementName) {

        var stringToInsert = String.format(".%s", elementName);
        var insertionIndex = parentConfigurePath.length() - 1;

        var buffer = new StringBuilder(parentConfigurePath);
        buffer.insert(insertionIndex, stringToInsert);
        return buffer.toString();
    }

    private void logChangeInExecutablePath(@NotNull StringWithDefault executablePath) {

        var executableStatus = refreshExecutableStatus(executablePath);

        logChangeInExecutablePath(executableStatus, executablePath);
    }

    private void logChangeInExecutablePath(@NotNull CliExecutableStatus executableStatus, @NotNull StringWithDefault executablePath) {

        String message;
        CliLogEntryType type;
        var path = executablePath.getActualValue();
        switch (executableStatus.getCompatibility()) {
            case INCOMPATIBLE -> {
                message = AutomateBundle.message("general.AutomateCliService.ExecutablePathChanged.UnSupported.Message", path,
                                                 executableStatus.getMinCompatibleVersion());
                type = CliLogEntryType.ERROR;
            }
            case COMPATIBLE -> {
                message = AutomateBundle.message("general.AutomateCliService.ExecutablePathChanged.Supported.Message", path);
                type = CliLogEntryType.NORMAL;
            }
            default -> {
                message = AutomateBundle.message("general.AutomateCliService.ExecutablePathChanged.Unknown.Message", path);
                type = CliLogEntryType.ERROR;
            }
        }

        var entry = new CliLogEntry(message, type);
        this.cliRunner.log(entry);
    }

    @NotNull
    private CliExecutableStatus refreshExecutableStatus(@NotNull StringWithDefault executablePath) {

        var currentDirectory = this.platform.getDotNetInstallationDirectory();
        var executableStatus = tryGetExecutableStatus(currentDirectory, executablePath);
        saveStatusIfSupported(executableStatus);

        return executableStatus;
    }

    private void saveStatusIfSupported(CliExecutableStatus executableStatus) {

        this.cache.setIsCliInstalled(executableStatus.getCompatibility() == CliVersionCompatibility.COMPATIBLE);
    }
}
