package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntryType;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
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

    @NotNull
    @Override
    public CliTextResult execute(@NotNull String executablePath, @NotNull List<String> args) {

        try {
            var command = new ArrayList<String>();
            command.add(executablePath);
            command.addAll(args);
            var builder = new ProcessBuilder(command);
            builder.directory(new File(this.platform.getCurrentDirectory()));
            AddCliLogEntry(String.format("Command: %s", String.join(" ", args)), CliLogEntryType.Normal);

            var process = builder.start();
            var success = process.waitFor(5, TimeUnit.SECONDS);
            if (!success) {
                var error = String.format("Failed to start CLI at: %s", executablePath);
                AddCliLogEntry(error, CliLogEntryType.Error);
                return new CliTextResult(error, "");
            }
            var error = "";
            var output = "";
            if (process.exitValue() != 0) {
                var errorStream = process.getErrorStream();
                var errorBytes = errorStream.readAllBytes();
                errorStream.close();
                error = new String(errorBytes, StandardCharsets.UTF_8).trim();
                AddCliLogEntry(String.format("Command failed with error: %s", error), CliLogEntryType.Error);
            }
            else {
                var outputStream = process.getInputStream();
                var outputBytes = outputStream.readAllBytes();
                outputStream.close();
                output = new String(outputBytes, StandardCharsets.UTF_8).trim();
                AddCliLogEntry("Command executed successfully", CliLogEntryType.Success);
            }

            process.destroy();
            return new CliTextResult(error, output);

        } catch (InterruptedException | IOException e) {
            var error = String.format("Failed to run CLI with error: %s", e.getMessage());
            AddCliLogEntry(error, CliLogEntryType.Error);
            return new CliTextResult(error, "");
        }
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

    private void AddCliLogEntry(String text, CliLogEntryType type) {
        var oldValue = new ArrayList<>(cliLogs);

        var entry = new CliLogEntry(text, type);
        cliLogs.add(entry);

        var newValue = new ArrayList<>();
        newValue.add(entry);

        cliLogsListeners.firePropertyChange("CliLogs", oldValue, newValue);
    }

}
