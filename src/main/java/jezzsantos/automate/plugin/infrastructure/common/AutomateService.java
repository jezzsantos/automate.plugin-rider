package jezzsantos.automate.plugin.infrastructure.common;

import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;
import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;
import jezzsantos.automate.plugin.application.interfaces.ToolkitDefinition;
import jezzsantos.automate.plugin.application.services.interfaces.IAutomateService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AutomateService implements IAutomateService {

    private final Project project;

    public AutomateService(@NotNull Project project) {
        this.project = project;
    }

    @NotNull
    @Override
    public String getExecutableName() {
        return (IsWindowsOs()
                ? "automate.exe"
                : "automate");
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
    public String tryGetExecutableVersion(@Nullable String executablePath) {
        var result = runAutomateForTextOutput(executablePath, new ArrayList<>(List.of("--version")));
        if (result.isError()) {
            return null;
        }

        return result.output;
    }

    @NotNull
    @Override
    public List<PatternDefinition> getPatterns(@Nullable String executablePath) {
        var result = runAutomateForStructuredOutput(ListPatternsStructuredOutput.class, executablePath, new ArrayList<>(List.of("list", "patterns")));
        if (result.isError()) {
            return new ArrayList<>();
        } else {
            var patterns = result.output.getPatterns();
            return patterns != null ? patterns : new ArrayList<>();
        }
    }

    @NotNull
    @Override
    public List<ToolkitDefinition> getToolkits(@Nullable String executablePath) {
        var result = runAutomateForStructuredOutput(ListToolkitsStructuredOutput.class, executablePath, new ArrayList<>(List.of("list", "toolkits")));
        if (result.isError()) {
            return new ArrayList<>();
        } else {
            var toolkits = result.output.getToolkits();
            return toolkits != null ? toolkits : new ArrayList<>();
        }
    }

    @NotNull
    @Override
    public List<DraftDefinition> getDrafts(@Nullable String executablePath) {
        var result = runAutomateForStructuredOutput(ListDraftsStructuredOutput.class, executablePath, new ArrayList<>(List.of("list", "drafts")));
        if (result.isError()) {
            return new ArrayList<>();
        } else {
            var drafts = result.output.getDrafts();
            return drafts != null ? drafts : new ArrayList<>();
        }
    }

    @NotNull
    @Override
    public PatternDefinition addPattern(@NotNull String executablePath, @NotNull String name) throws Exception {
        var result = runAutomateForStructuredOutput(CreatePatternStructuredOutput.class, executablePath, new ArrayList<>(List.of("create", "pattern", name)));
        if (result.isError()) {
            throw new Exception(result.error);
        } else {
            return result.output.getPattern();
        }
    }

    @NotNull
    private <TResult> CliStructuredResult<TResult> runAutomateForStructuredOutput(Class<TResult> outputClass, String executablePath, List<String> args) {

        var allArgs = new ArrayList<>(args);
        if (!allArgs.contains("--output-structured")) {
            allArgs.add("--output-structured");
        }

        var gson = new Gson();
        var result = runAutomateForTextOutput(executablePath, allArgs);
        if (result.isError()) {
            var error = gson.fromJson(result.error, StructuredError.class);

            return new CliStructuredResult<>(error.Error.Message, null);
        }

        var output = gson.fromJson(result.output, outputClass);
        return new CliStructuredResult<>("", output);
    }


    @NotNull
    private CliTextResult runAutomateForTextOutput(String executablePath, List<String> args) {
        var path = executablePath == null || executablePath.isEmpty() ? getDefaultInstallLocation() : executablePath;

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
