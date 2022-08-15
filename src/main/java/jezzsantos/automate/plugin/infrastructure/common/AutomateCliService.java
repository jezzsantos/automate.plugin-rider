package jezzsantos.automate.plugin.infrastructure.common;

import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.AllDefinitions;
import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;
import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;
import jezzsantos.automate.plugin.application.interfaces.ToolkitDefinition;
import jezzsantos.automate.plugin.application.services.interfaces.IAutomateService;
import jezzsantos.automate.plugin.infrastructure.common.cli.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AutomateCliService implements IAutomateService {

    @NotNull
    private final Project project;
    private final IAutomationCache cache;

    public AutomateCliService(@NotNull Project project) {
        this.project = project;
        this.cache = new InMemAutomationCache();
    }

    @NotNull
    @Override
    public String getExecutableName() {
        return (IsWindowsOs()
                ? String.format("%s.exe", AutomateConstants.ExecutableName)
                : AutomateConstants.ExecutableName);
    }

    @NotNull
    @Override
    public String getDefaultInstallLocation() {
        return (IsWindowsOs()
                ? System.getenv("USERPROFILE") + "\\.dotnet\\tools\\"
                : System.getProperty("user.home") + "/.dotnet/tools/") + this.getExecutableName();
    }

    @Nullable
    @Override
    public String tryGetExecutableVersion(@NotNull String executablePath) {
        var result = runAutomateForTextOutput(executablePath, new ArrayList<>(List.of("--version")));
        if (result.isError()) {
            return null;
        }

        return result.output;
    }

    @NotNull
    @Override
    public AllDefinitions getAllAutomation(@NotNull String executablePath) {

        return cache.ListAll(() -> {
            var result = runAutomateForStructuredOutput(ListAllDefinitionsStructuredOutput.class, executablePath, new ArrayList<>(List.of("list", "all")));
            if (result.isError()) {
                return new AllDefinitions();
            } else {
                return result.output.getAll();
            }
        });
    }

    @NotNull
    @Override
    public List<PatternDefinition> getPatterns(@NotNull String executablePath) {
        return cache.ListPatterns(() -> {
            var result = runAutomateForStructuredOutput(ListPatternsStructuredOutput.class, executablePath, new ArrayList<>(List.of("list", "patterns")));
            if (result.isError()) {
                return new ArrayList<>();
            } else {
                var patterns = result.output.getPatterns();
                return patterns != null ? patterns : new ArrayList<>();
            }
        });
    }

    @NotNull
    @Override
    public List<ToolkitDefinition> getToolkits(@NotNull String executablePath) {
        return cache.ListToolkits(() -> {
            var result = runAutomateForStructuredOutput(ListToolkitsStructuredOutput.class, executablePath, new ArrayList<>(List.of("list", "toolkits")));
            if (result.isError()) {
                return new ArrayList<>();
            } else {
                var toolkits = result.output.getToolkits();
                return toolkits != null ? toolkits : new ArrayList<>();
            }
        });
    }

    @NotNull
    @Override
    public List<DraftDefinition> getDrafts(@NotNull String executablePath) {
        return cache.ListDrafts(() -> {
            var result = runAutomateForStructuredOutput(ListDraftsStructuredOutput.class, executablePath, new ArrayList<>(List.of("list", "drafts")));
            if (result.isError()) {
                return new ArrayList<>();
            } else {
                var drafts = result.output.getDrafts();
                return drafts != null ? drafts : new ArrayList<>();
            }
        });
    }

    @NotNull
    @Override
    public PatternDefinition createPattern(@NotNull String executablePath, @NotNull String name) throws Exception {
        var result = runAutomateForStructuredOutput(CreatePatternStructuredOutput.class, executablePath, new ArrayList<>(List.of("create", "pattern", name)));
        if (result.isError()) {
            throw new Exception(result.error);
        } else {
            cache.invalidatePatternList();
            return result.output.getPattern();
        }
    }

    @Override
    public void setCurrentPattern(@NotNull String executablePath, @NotNull String id) throws Exception {
        var result = runAutomateForStructuredOutput(SwitchPatternStructuredOutput.class, executablePath, new ArrayList<>(List.of("edit", "switch", id)));
        if (result.isError()) {
            throw new Exception(result.error);
        } else {
            cache.invalidatePatternList();
            result.output.getPattern();
        }
    }

    @Nullable
    @Override
    public PatternDefinition getCurrentPattern(@NotNull String executablePath) {
        return this.cache.GetPattern(() ->
        {
            var patterns = getPatterns(executablePath);

            return patterns.stream()
                    .filter(PatternDefinition::getIsCurrent)
                    .findFirst().orElse(null);
        });
    }

    @Nullable
    @Override
    public DraftDefinition getCurrentDraft(@NotNull String executablePath) {
        return this.cache.GetDraft(() ->
        {
            var drafts = getDrafts(executablePath);

            return drafts.stream()
                    .filter(DraftDefinition::getIsCurrent)
                    .findFirst().orElse(null);
        });
    }

    @Override
    public void setCurrentDraft(@NotNull String executablePath, @NotNull String id) throws Exception {
        var result = runAutomateForStructuredOutput(SwitchDraftStructuredOutput.class, executablePath, new ArrayList<>(List.of("run", "switch", id)));
        if (result.isError()) {
            throw new Exception(result.error);
        } else {
            cache.invalidateDraftList();
            result.output.getDraft();
        }
    }

    @NotNull
    @Override
    public DraftDefinition createDraft(@NotNull String executablePath, @NotNull String toolkitName, @NotNull String name) throws Exception {
        var result = runAutomateForStructuredOutput(CreateDraftStructuredOutput.class, executablePath, new ArrayList<>(List.of("run", "toolkit", toolkitName, "--name", name)));
        if (result.isError()) {
            throw new Exception(result.error);
        } else {
            cache.invalidateDraftList();
            return result.output.getDraft();
        }
    }

    @Override
    public void installToolkit(@NotNull String executablePath, @NotNull String location) throws Exception {
        var result = runAutomateForStructuredOutput(InstallToolkitStructuredOutput.class, executablePath, new ArrayList<>(List.of("install", "toolkit", location)));
        if (result.isError()) {
            throw new Exception(result.error);
        }
        cache.invalidateToolkitList();
    }

    @NotNull
    private <TResult> CliStructuredResult<TResult> runAutomateForStructuredOutput(@NotNull Class<TResult> outputClass, @NotNull String executablePath, @NotNull List<String> args) {

        var allArgs = new ArrayList<>(args);
        if (!allArgs.contains("--output-structured")) {
            allArgs.add("--output-structured");
        }

        var gson = new Gson();
        var result = runAutomateForTextOutput(executablePath, allArgs);
        if (result.isError()) {
            return new CliStructuredResult<>(result.error, null);
        }

        var output = gson.fromJson(result.output, outputClass);
        return new CliStructuredResult<>("", output);
    }


    @NotNull
    private CliTextResult runAutomateForTextOutput(@NotNull String executablePath, @NotNull List<String> args) {
        var path = executablePath.isEmpty() ? getDefaultInstallLocation() : executablePath;

        try {
            var command = new ArrayList<String>();
            command.add(path);
            command.addAll(args);
            var builder = new ProcessBuilder(command);
            builder.directory(new File(Objects.requireNonNull(project.getBasePath())));
            var process = builder.start();
            var success = process.waitFor(5, TimeUnit.SECONDS);
            if (!success) {
                return new CliTextResult("CLI failed to execute", "");
            }
            var error = "";
            var output = "";
            if (process.exitValue() != 0) {
                var errorStream = process.getErrorStream();
                var errorBytes = errorStream.readAllBytes();
                errorStream.close();
                error = new String(errorBytes, StandardCharsets.UTF_8).trim();
            } else {
                var outputStream = process.getInputStream();
                var outputBytes = outputStream.readAllBytes();
                outputStream.close();
                output = new String(outputBytes, StandardCharsets.UTF_8).trim();
            }

            process.destroy();
            return new CliTextResult(error, output);

        } catch (InterruptedException | IOException e) {
            return new CliTextResult(String.format("CLI failed to execute. Error was: %s", e.getMessage()), "");
        }
    }


    private Boolean IsWindowsOs() {
        return System.getProperty("os.name").startsWith("Windows");
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

    private static class CliTextResult {
        private final String error;
        private final String output;

        public CliTextResult(@NotNull String error, @NotNull String output) {
            this.error = error;
            this.output = output;
        }

        public Boolean isError() {
            return !this.error.isEmpty() && this.output.isEmpty();
        }
    }
}