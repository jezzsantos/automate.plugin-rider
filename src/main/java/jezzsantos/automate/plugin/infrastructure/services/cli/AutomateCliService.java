package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.*;
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

    public AutomateCliService(@NotNull IConfiguration configuration, @NotNull IAutomationCache cache, @NotNull IOsPlatform platform) {
        this(configuration, cache, platform, new AutomateCliRunner(platform));
    }

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
    public AllDefinitions listAllAutomation(boolean forceRefresh) {
        var executablePath = this.configuration.getExecutablePath();
        return cache.ListAll(() -> {
            var result = runAutomateForStructuredOutput(ListAllDefinitionsStructuredOutput.class, executablePath, new ArrayList<>(List.of("list", "all")));
            if (result.isError()) {
                return new AllDefinitions();
            }
            else {
                return result.output.getAll();
            }
        }, forceRefresh);
    }

    @NotNull
    @Override
    public List<PatternDefinition> listPatterns() {
        var executablePath = this.configuration.getExecutablePath();
        return cache.ListPatterns(() -> {
            var result = runAutomateForStructuredOutput(ListPatternsStructuredOutput.class, executablePath, new ArrayList<>(List.of("list", "patterns")));
            if (result.isError()) {
                return new ArrayList<>();
            }
            else {
                var patterns = result.output.getPatterns();
                return patterns != null
                        ? patterns
                        : new ArrayList<>();
            }
        });
    }

    @NotNull
    @Override
    public List<ToolkitDefinition> listToolkits() {
        var executablePath = this.configuration.getExecutablePath();
        return cache.ListToolkits(() -> {
            var result = runAutomateForStructuredOutput(ListToolkitsStructuredOutput.class, executablePath, new ArrayList<>(List.of("list", "toolkits")));
            if (result.isError()) {
                return new ArrayList<>();
            }
            else {
                var toolkits = result.output.getToolkits();
                return toolkits != null
                        ? toolkits
                        : new ArrayList<>();
            }
        });
    }

    @NotNull
    @Override
    public List<DraftDefinition> listDrafts() {
        var executablePath = this.configuration.getExecutablePath();
        return cache.ListDrafts(() -> {
            var result = runAutomateForStructuredOutput(ListDraftsStructuredOutput.class, executablePath, new ArrayList<>(List.of("list", "drafts")));
            if (result.isError()) {
                return new ArrayList<>();
            }
            else {
                var drafts = result.output.getDrafts();
                return drafts != null
                        ? drafts
                        : new ArrayList<>();
            }
        });
    }

    @NotNull
    @Override
    public PatternDefinition createPattern(@NotNull String name) throws Exception {
        var executablePath = this.configuration.getExecutablePath();
        var result = runAutomateForStructuredOutput(CreatePatternStructuredOutput.class, executablePath, new ArrayList<>(List.of("create", "pattern", name)));
        if (result.isError()) {
            throw new Exception(result.error);
        }
        else {
            cache.invalidatePatternList();
            return result.output.getPattern();
        }
    }

    @Nullable
    @Override
    public PatternDefinition getCurrentPattern() {
        return this.cache.GetPattern(() -> {
            var patterns = listPatterns();

            return patterns.stream().filter(PatternDefinition::getIsCurrent).findFirst().orElse(null);
        });
    }

    @Override
    public void setCurrentPattern(@NotNull String id) throws Exception {
        var executablePath = this.configuration.getExecutablePath();
        var result = runAutomateForStructuredOutput(SwitchPatternStructuredOutput.class, executablePath, new ArrayList<>(List.of("edit", "switch", id)));
        if (result.isError()) {
            throw new Exception(result.error);
        }
        else {
            cache.invalidatePatternList();
            result.output.getPattern();
        }
    }

    @Nullable
    @Override
    public DraftDefinition getCurrentDraft() {
        return this.cache.GetDraft(() -> {
            var drafts = listDrafts();

            return drafts.stream().filter(DraftDefinition::getIsCurrent).findFirst().orElse(null);
        });
    }

    @Override
    public void setCurrentDraft(@NotNull String id) throws Exception {
        var executablePath = this.configuration.getExecutablePath();
        var result = runAutomateForStructuredOutput(SwitchDraftStructuredOutput.class, executablePath, new ArrayList<>(List.of("run", "switch", id)));
        if (result.isError()) {
            throw new Exception(result.error);
        }
        else {
            cache.invalidateDraftList();
            result.output.getDraft();
        }
    }

    @NotNull
    @Override
    public DraftDefinition createDraft(@NotNull String toolkitName, @NotNull String name) throws Exception {
        var executablePath = this.configuration.getExecutablePath();
        var result = runAutomateForStructuredOutput(CreateDraftStructuredOutput.class, executablePath, new ArrayList<>(List.of("run", "toolkit", toolkitName, "--name", name)));
        if (result.isError()) {
            throw new Exception(result.error);
        }
        else {
            cache.invalidateDraftList();
            return result.output.getDraft();
        }
    }

    @Override
    public void installToolkit(@NotNull String location) throws Exception {
        var executablePath = this.configuration.getExecutablePath();
        var result = runAutomateForStructuredOutput(InstallToolkitStructuredOutput.class, executablePath, new ArrayList<>(List.of("install", "toolkit", location)));
        if (result.isError()) {
            throw new Exception(result.error);
        }
        cache.invalidateToolkitList();
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

    @NotNull
    private <TResult> CliStructuredResult<TResult> runAutomateForStructuredOutput(@NotNull Class<TResult> outputClass, @NotNull String executablePath, @NotNull List<String> args) {

        var allArgs = new ArrayList<>(args);
        if (!allArgs.contains("--output-structured")) {
            allArgs.add("--output-structured");
        }

        var gson = new Gson();
        var result = cliRunner.execute(getExecutablePathSafe(executablePath), allArgs);
        if (result.isError()) {
            return new CliStructuredResult<>(result.getError(), null);
        }

        var output = gson.fromJson(result.getOutput(), outputClass);
        return new CliStructuredResult<>("", output);
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

    private static class CliStructuredResult<TResult> {

        private final String error;
        private final TResult output;

        public CliStructuredResult(@NotNull String error, TResult output) {
            this.error = error;
            this.output = output;
        }

        public Boolean isError() {
            return !this.error.isEmpty();
        }
    }

}
