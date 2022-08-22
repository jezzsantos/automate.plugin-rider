package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.intellij.openapi.project.Project;
import com.intellij.serviceContainer.NonInjectable;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntryType;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftDetailed;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternDetailed;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
import jezzsantos.automate.plugin.application.services.interfaces.IAutomateService;
import jezzsantos.automate.plugin.application.services.interfaces.IConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AutomateCliService implements IAutomateService {

    @NotNull
    private final IAutomationCache cache;
    @NotNull
    private final IConfiguration configuration;
    @NotNull
    private final IOsPlatform platform;

    @NotNull
    private final IAutomateCliRunner cliRunner;

    @UsedImplicitly
    public AutomateCliService(@NotNull Project project) {
        this(project.getService(IConfiguration.class), new InMemAutomationCache(), new OsPlatform(project));
    }

    @NonInjectable
    public AutomateCliService(@NotNull IConfiguration configuration, @NotNull IAutomationCache cache, @NotNull IOsPlatform platform) {
        this(configuration, cache, platform, new AutomateCliRunner(platform));
    }

    @NonInjectable
    public AutomateCliService(@NotNull IConfiguration configuration, @NotNull IAutomationCache cache, @NotNull IOsPlatform platform, @NotNull IAutomateCliRunner runner) {
        this.configuration = configuration;
        this.cache = cache;
        this.platform = platform;
        this.cliRunner = runner;

        this.configuration.addListener(e -> {

            if (e.getPropertyName().equals("executablePath")) {
                var path = (String) e.getNewValue();
                logChangeInExecutablePath(path);
            }
        });
        var executablePath = getExecutablePathSafe(this.configuration.getExecutablePath());
        logChangeInExecutablePath(executablePath);
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
    public String getDefaultInstallLocation() {
        return Paths.get(this.platform.getDotNetInstallationDirectory()).resolve(this.getExecutableName()).toString();
    }

    @Nullable
    @Override
    public String tryGetExecutableVersion(@NotNull String executablePath) {
        var result = this.cliRunner.execute(getExecutablePathSafe(executablePath), new ArrayList<>(List.of("--version")));
        if (result.isError()) {
            return null;
        }

        return result.getOutput();
    }

    @NotNull
    @Override
    public AllStateLite listAllAutomation(boolean forceRefresh) {
        return cache.ListAll(() -> {
            var result = runAutomateForStructuredOutput(ListAllDefinitionsStructuredOutput.class, new ArrayList<>(List.of("list", "all")));
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
    public List<PatternLite> listPatterns() {
        return cache.ListPatterns(() -> {
            var result = runAutomateForStructuredOutput(ListPatternsStructuredOutput.class, new ArrayList<>(List.of("list", "patterns")));
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
    public List<ToolkitLite> listToolkits() {
        return cache.ListToolkits(() -> {
            var result = runAutomateForStructuredOutput(ListToolkitsStructuredOutput.class, new ArrayList<>(List.of("list", "toolkits")));
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
    public List<DraftLite> listDrafts() {
        return cache.ListDrafts(() -> {
            var result = runAutomateForStructuredOutput(ListDraftsStructuredOutput.class, new ArrayList<>(List.of("list", "drafts")));
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
    public PatternLite createPattern(@NotNull String name) throws Exception {
        var result = runAutomateForStructuredOutput(CreatePatternStructuredOutput.class, new ArrayList<>(List.of("create", "pattern", name)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            cache.invalidateAllPatterns();
            return result.getOutput().getPattern();
        }
    }

    @NotNull
    @Override
    public PatternDetailed getCurrentPatternDetailed() throws Exception {
        return this.cache.GetPatternDetailed(() -> {
            var result = runAutomateForStructuredOutput(GetPatternStructuredOutput.class, new ArrayList<>(List.of("view", "pattern", "--all")));
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
    public PatternLite getCurrentPatternInfo() {
        return this.cache.GetPatternInfo(() -> {
            var patterns = listPatterns();

            return patterns.stream().filter(PatternLite::getIsCurrent).findFirst().orElse(null);
        });
    }

    @Override
    public void setCurrentPattern(@NotNull String id) throws Exception {
        var result = runAutomateForStructuredOutput(SwitchPatternStructuredOutput.class, new ArrayList<>(List.of("edit", "switch", id)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            cache.invalidateAllPatterns();
            result.getOutput().getPattern();
        }
    }

    @NotNull
    @Override
    public DraftDetailed getCurrentDraftDetailed() throws Exception {
        return this.cache.GetDraftDetailed(() -> {
            var result = runAutomateForStructuredOutput(GetDraftStructuredOutput.class, new ArrayList<>(List.of("view", "draft")));
            if (result.isError()) {
                throw new Exception(result.getError().getErrorMessage());
            }
            else {
                return result.getOutput().getDraft();
            }
        });
    }

    @Nullable
    @Override
    public DraftLite getCurrentDraftInfo() {
        return this.cache.GetDraftInfo(() -> {
            var drafts = listDrafts();

            return drafts.stream().filter(DraftLite::getIsCurrent).findFirst().orElse(null);
        });
    }

    @Override
    public void setCurrentDraft(@NotNull String id) throws Exception {
        var result = runAutomateForStructuredOutput(SwitchDraftStructuredOutput.class, new ArrayList<>(List.of("run", "switch", id)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            cache.invalidateAllDrafts();
            result.getOutput().getDraft();
        }
    }

    @NotNull
    @Override
    public DraftLite createDraft(@NotNull String toolkitName, @NotNull String name) throws Exception {
        var result = runAutomateForStructuredOutput(CreateDraftStructuredOutput.class, new ArrayList<>(List.of("run", "toolkit", toolkitName, "--name", name)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            cache.invalidateAllDrafts();
            return result.getOutput().getDraft();
        }
    }

    @Override
    public void installToolkit(@NotNull String location) throws Exception {
        var result = runAutomateForStructuredOutput(InstallToolkitStructuredOutput.class, new ArrayList<>(List.of("install", "toolkit", location)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        cache.invalidateAllToolkits();
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
    public List<CliLogEntry> getCliLog() {
        return cliRunner.getCliLogs();
    }

    @Override
    public Attribute addAttribute(@NotNull String name, boolean isRequired, @NotNull String type, @Nullable String defaultValue, @Nullable List<String> choices) throws Exception {
        var args = new ArrayList<>(List.of("edit", "add-attribute", name, "--isrequired", Boolean.toString(isRequired), "--isoftype", type));
        if (defaultValue != null) {
            args.addAll(List.of("--defaultvalueis", defaultValue));
        }
        if (choices != null && !choices.isEmpty()) {
            args.addAll(List.of("--isoneof", String.join(";", choices)));
        }
        var result = runAutomateForStructuredOutput(AddRemoveAttributeStructuredOutput.class, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            cache.invalidateCurrentPattern();
            var attribute = result.getOutput().getAttribute();
            attribute.setProperties(isRequired, type, defaultValue, choices);
            return attribute;
        }
    }

    @Override
    public void deleteAttribute(@NotNull String name) throws Exception {
        var result = runAutomateForStructuredOutput(AddRemoveAttributeStructuredOutput.class, new ArrayList<>(List.of("edit", "delete-attribute", name)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            cache.invalidateCurrentPattern();
        }
    }

    @NotNull
    private <TResult> CliStructuredResult<TResult> runAutomateForStructuredOutput(@NotNull Class<TResult> outputClass, @NotNull List<String> args) {
        var executablePath = this.configuration.getExecutablePath();
        return cliRunner.executeStructured(outputClass, getExecutablePathSafe(executablePath), args);
    }

    @NotNull
    private String getExecutablePathSafe(@NotNull String executablePath) {
        return executablePath.isEmpty()
                ? getDefaultInstallLocation()
                : executablePath;
    }

    private void logChangeInExecutablePath(String path) {
        var entry = new CliLogEntry(String.format("Using CLI at: %s", path), CliLogEntryType.Normal);
        this.cliRunner.addCliLogEntry(entry);
    }
}
