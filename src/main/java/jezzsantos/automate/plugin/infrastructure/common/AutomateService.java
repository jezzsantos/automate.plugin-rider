package jezzsantos.automate.plugin.infrastructure.common;

import com.google.gson.Gson;
import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.interfaces.*;
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
        return runAutomate(executablePath, new ArrayList<>(List.of("--version")));
    }

    @NotNull
    @Override
    public List<PatternDefinition> getPatterns(@Nullable String executablePath) {
        var json = runAutomate(executablePath, new ArrayList<>(List.of("list", "patterns", "--output-structured")));
        var output = new Gson().fromJson(json, ListPatternsStructuredOutput.class);

        if (output.Output.get(0).Values.Patterns == null) {
            return new ArrayList<>();
        } else {
            return output.Output.get(0).Values.Patterns;
        }
    }

    @NotNull
    @Override
    public List<ToolkitDefinition> getToolkits(@Nullable String executablePath) {
        var json = runAutomate(executablePath, new ArrayList<>(List.of("list", "Toolkits", "--output-structured")));
        var output = new Gson().fromJson(json, ListToolkitsStructuredOutput.class);

        if (output.Output.get(0).Values.Toolkits == null) {
            return new ArrayList<>();
        } else {
            return output.Output.get(0).Values.Toolkits;
        }
    }

    @NotNull
    @Override
    public List<DraftDefinition> getDrafts(@Nullable String executablePath) {
        var json = runAutomate(executablePath, new ArrayList<>(List.of("list", "drafts", "--output-structured")));
        var output = new Gson().fromJson(json, ListDraftsStructuredOutput.class);

        if (output.Output.get(0).Values.Drafts == null) {
            return new ArrayList<>();
        } else {
            return output.Output.get(0).Values.Drafts;
        }
    }


    @Nullable
    private String runAutomate(String executablePath, List<String> args) {
        var path = executablePath == null || executablePath.isEmpty() ? getDefaultInstallLocation() : executablePath;

        String result;
        try {
            var command = new ArrayList<String>();
            command.add(path);
            command.addAll(args);
            var builder = new ProcessBuilder(command);
            builder.directory(new File(Objects.requireNonNull(project.getBasePath())));
            var process = builder.start();
            var success = process.waitFor(5, TimeUnit.SECONDS);
            if (!success) {
                return null;
            }
            if (process.exitValue() != 0) {
                return null;
            } else {
                var outputStream = process.getInputStream();
                var output = outputStream.readAllBytes();
                outputStream.close();
                result = new String(output, StandardCharsets.UTF_8).trim();
            }

            process.destroy();
            return result;

        } catch (InterruptedException | IOException e) {
            return null;
        }
    }


    private Boolean IsWindowsOs() {
        return System.getProperty("os.name").startsWith("Windows");
    }
}
