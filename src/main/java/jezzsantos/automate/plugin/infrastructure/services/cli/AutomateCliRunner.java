package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.google.gson.Gson;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntryType;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AutomateCliRunner implements IAutomateCliRunner {

    @NotNull
    private final IOsPlatform platform;
    @NotNull
    private final List<CliLogEntry> cliLogs = new ArrayList<>();
    @NotNull
    private final PropertyChangeSupport cliLogsListeners = new PropertyChangeSupport(this);

    public AutomateCliRunner(@NotNull IOsPlatform platform) {
        this.platform = platform;
    }

    private static StructuredError getStructuredError(String error) {
        var gson = new Gson();
        return gson.fromJson(error, StructuredError.class);
    }

    private static <TResult> TResult getStructuredOutput(Class<TResult> outputClass, String output) {
        var gson = new Gson();
        return gson.fromJson(output, outputClass);
    }

    @NotNull
    @Override
    public <TResult> CliStructuredResult<TResult> executeStructured(@NotNull Class<TResult> outputClass, @NotNull String executablePath, @NotNull List<String> args) {
        var result = executeInternal(executablePath, args, true);
        if (result.isError()) {
            return new CliStructuredResult<>(getStructuredError(result.getError()), null);
        }

        return new CliStructuredResult<>(null, getStructuredOutput(outputClass, result.getOutput()));
    }

    @NotNull
    @Override
    public CliTextResult execute(@NotNull String executablePath, @NotNull List<String> args) {
        return executeInternal(executablePath, args, false);
    }

    @Override
    public @NotNull List<CliLogEntry> getCliLogs() {
        return this.cliLogs;
    }

    @Override
    public void addLogListener(@NotNull PropertyChangeListener listener) {
        this.cliLogsListeners.addPropertyChangeListener(listener);
    }

    @Override
    public void removeLogListener(@NotNull PropertyChangeListener listener) {
        this.cliLogsListeners.removePropertyChangeListener(listener);
    }

    @Override
    public void addCliLogEntry(@NotNull CliLogEntry entry) {
        cliLogs.add(entry);
    }

    private CliTextResult executeInternal(@NotNull String executablePath, @NotNull List<String> args, boolean isStructured) {

        try {
            var command = new ArrayList<String>();
            command.add(executablePath);
            command.addAll(args);
            if (isStructured && !command.contains("--os")) {
                command.add("--os");
            }

            var builder = new ProcessBuilder(command);
            builder.redirectErrorStream(false);

            builder.directory(new File(this.platform.getCurrentDirectory()));
            AddCliLogEntry(String.format("Command: %s", String.join(" ", args)), CliLogEntryType.Normal);

            var process = builder.start();
            final var stdOutWriter = new StringWriter();
            final var stdErrWriter = new StringWriter();
            new Thread(() -> {
                try {
                    IOUtils.copy(process.getInputStream(), stdOutWriter, StandardCharsets.UTF_8);
                    IOUtils.copy(process.getErrorStream(), stdErrWriter, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            var success = process.waitFor(15, TimeUnit.SECONDS);
            if (!success) {
                var error = String.format("Failed to start CLI at: %s", executablePath);
                AddCliLogEntry(error, CliLogEntryType.Error);
                return new CliTextResult(error, "");
            }
            var stdErr = stdErrWriter.toString().trim();
            var stdOut = stdOutWriter.toString().trim();
            if (process.exitValue() != 0) {
                var error = isStructured
                        ? getStructuredError(stdErr).getErrorMessage()
                        : stdErr;
                AddCliLogEntry(String.format("Command failed with error: %s", error), CliLogEntryType.Error);
            }
            else {
                AddCliLogEntry("Command executed successfully", CliLogEntryType.Success);
            }

            process.destroy();
            return new CliTextResult(stdErr, stdOut);

        } catch (InterruptedException | IOException e) {
            var error = String.format("Failed to run CLI with error: %s", e.getMessage());
            AddCliLogEntry(error, CliLogEntryType.Error);
            return new CliTextResult(error, "");
        }
    }

    private void AddCliLogEntry(String text, CliLogEntryType type) {
        var oldValue = new ArrayList<>(cliLogs);

        var entry = new CliLogEntry(text, type);
        cliLogs.add(entry);

        var newValue = new ArrayList<>();
        newValue.add(entry);

        cliLogsListeners.firePropertyChange("CliLogs", oldValue, newValue);
    }

}
