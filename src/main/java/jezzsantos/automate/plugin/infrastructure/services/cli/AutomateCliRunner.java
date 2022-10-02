package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.Disposable;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntryType;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.StringWithDefault;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.module.ModuleDescriptor;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

enum FailureCause {
    FailedToStart,
    ThrewException,
    FailedWithError
}

interface IProcessRunner extends Disposable {

    ProcessResult start(@NotNull List<String> commandLineAndArguments, @NotNull String currentDirectory);
}

class ProcessResult {

    private boolean success;
    private String output;
    private String error;
    private Exception exception;
    private FailureCause cause;

    public static ProcessResult createSuccess(@NotNull String output) {

        var result = new ProcessResult();
        result.success = true;
        result.output = output;
        result.cause = null;

        return result;
    }

    public static ProcessResult createFailedToStart() {

        var result = new ProcessResult();
        result.success = false;
        result.cause = FailureCause.FailedToStart;

        return result;
    }

    public static ProcessResult createFailedWithError(@NotNull String error) {

        var result = new ProcessResult();
        result.success = false;
        result.error = error;
        result.cause = FailureCause.FailedWithError;

        return result;
    }

    public static ProcessResult createFailedWithException(@NotNull Exception e) {

        var result = new ProcessResult();
        result.success = false;
        result.exception = e;
        result.cause = FailureCause.ThrewException;

        return result;
    }

    public boolean getSuccess() {

        return this.success;
    }

    @Nullable
    public String getOutput() {

        if (!this.success) {
            return null;
        }

        return this.output;
    }

    @Nullable
    public String getError() {

        if (this.success) {
            return null;
        }

        return this.error;
    }

    @Nullable
    public Exception getException() {

        if (this.success) {
            return null;
        }

        return this.exception;
    }

    @Nullable
    public FailureCause getFailureCause() {

        if (this.success) {
            return null;
        }

        return this.cause;
    }
}

class ProcessRunner implements IProcessRunner {

    @Override
    public void dispose() {

    }

    @Override
    public ProcessResult start(@NotNull List<String> commandLineAndArguments, @NotNull String currentDirectory) {

        var builder = new ProcessBuilder(commandLineAndArguments);
        builder.redirectErrorStream(false);

        builder.directory(new File(currentDirectory));

        Process process = null;
        try {
            process = builder.start();
            final var stdOutWriter = new StringWriter();
            final var stdErrWriter = new StringWriter();
            Process finalProcess = process;
            new Thread(() -> {
                try {
                    IOUtils.copy(finalProcess.getInputStream(), stdOutWriter, StandardCharsets.UTF_8);
                    IOUtils.copy(finalProcess.getErrorStream(), stdErrWriter, StandardCharsets.UTF_8);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();

            var success = process.waitFor(5, TimeUnit.SECONDS);
            if (!success) {
                return ProcessResult.createFailedToStart();
            }
            var stdErr = stdErrWriter.toString().trim();
            var stdOut = stdOutWriter.toString().trim();
            if (process.exitValue() != 0) {
                return ProcessResult.createFailedWithError(stdErr);
            }
            else {
                return ProcessResult.createSuccess(stdOut);
            }
        } catch (InterruptedException | IOException e) {
            return ProcessResult.createFailedWithException(e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}

public class AutomateCliRunner implements IAutomateCliRunner {

    public static final String PropertyChanged_Logs = "CliLogs";
    @NotNull
    private final IProcessRunner processRunner;
    @NotNull
    private final List<CliLogEntry> logs = new ArrayList<>();
    @NotNull
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    public AutomateCliRunner() {

        this(new ProcessRunner());
    }

    @TestOnly
    public AutomateCliRunner(@NotNull IProcessRunner processRunner) {

        this.processRunner = processRunner;
    }

    @NotNull
    @Override
    public CliTextResult execute(@NotNull String currentDirectory, @NotNull StringWithDefault executablePath, @NotNull List<String> args) {

        return executeInternal(currentDirectory, executablePath, args, false);
    }

    @NotNull
    @Override
    public <TResult extends StructuredOutput<?>> CliStructuredResult<TResult> executeStructured(@NotNull Class<TResult> outputClass, @NotNull String currentDirectory, @NotNull StringWithDefault executablePath, @NotNull List<String> args) {

        var result = executeInternal(currentDirectory, executablePath, args, true);
        if (result.isError()) {
            return new CliStructuredResult<>(getStructuredError(result.getError()), null);
        }

        return new CliStructuredResult<>(null, getStructuredOutput(outputClass, result.getOutput()));
    }

    @Nullable
    @Override
    public ModuleDescriptor.Version installLatest(@NotNull String currentDirectory, boolean uninstall) {

        if (uninstall) {
            var uninstallResult = uninstallCli(currentDirectory);
            if (uninstallResult.isError()) {
                return null;
            }

            logEntry(AutomateBundle.message("general.AutomateCliRunner.UninstallCommand.Outcome.Success.Message"), CliLogEntryType.SUCCESS);
        }

        var result = installCli(currentDirectory);
        if (result.isError()) {
            return null;
        }

        var version = getVersionFromOutput(result.getOutput());
        if (version == null) {
            logEntry(AutomateBundle.message("general.AutomateCliRunner.InstallCommand.ParseVersion.Message"), CliLogEntryType.ERROR);
            throw new RuntimeException(AutomateBundle.message("general.AutomateCliRunner.InstallCommand.ParseVersion.Message"));
        }

        logEntry(AutomateBundle.message("general.AutomateCliRunner.InstallCommand.Outcome.Success.Message", version), CliLogEntryType.SUCCESS);

        return ModuleDescriptor.Version.parse(version);
    }

    @Override
    public @NotNull List<CliLogEntry> getLogs() {

        return this.logs;
    }

    @Override
    public void addLogListener(@NotNull PropertyChangeListener listener) {

        this.listeners.addPropertyChangeListener(listener);
    }

    @Override
    public void removeLogListener(@NotNull PropertyChangeListener listener) {

        this.listeners.removePropertyChangeListener(listener);
    }

    @Override
    public void log(@NotNull CliLogEntry entry) {

        logEntry(entry.Text, entry.Type);
    }

    @NotNull
    private static StructuredError getStructuredError(String error) {

        var gson = new Gson();
        try {
            return gson.fromJson(error, StructuredError.class);
        } catch (JsonSyntaxException ex) {
            return new StructuredError(error);
        }
    }

    @NotNull
    private static <TResult> TResult getStructuredOutput(Class<TResult> outputClass, String output) {

        var gson = new Gson();
        return gson.fromJson(output, outputClass);
    }

    @Nullable
    private String getVersionFromOutput(@NotNull String output) {

        @SuppressWarnings("RegExpSimplifiable")
        var pattern = Pattern.compile("\\(version '(?<ver>[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}[\\-\\w]*)'\\)");
        var expression = pattern.matcher(output);
        if (!expression.find()) {
            return null;
        }

        return expression.group("ver");
    }

    @NotNull
    private CliTextResult uninstallCli(@NotNull String currentDirectory) {

        var command = new ArrayList<String>() {{
            add("dotnet");
            add("tool");
            add("uninstall");
            add(AutomateConstants.ExecutableName);
            add("--global");
        }};

        logEntry(AutomateBundle.message("general.AutomateCliRunner.UninstallCommand.Started.Message"), CliLogEntryType.NORMAL);

        var result = this.processRunner.start(command, currentDirectory);
        if (result.getSuccess()) {
            var output = Objects.requireNonNull(result.getOutput());
            return new CliTextResult("", output);
        }
        else {
            var cause = Objects.requireNonNull(result.getFailureCause());
            switch (cause) {
                case FailedToStart -> {
                    var error = AutomateBundle.message("general.AutomateCliRunner.UninstallCommand.Outcome.FailedToStart.Message");
                    logEntry(error, CliLogEntryType.ERROR);
                    return new CliTextResult(error, "");
                }
                case ThrewException -> {
                    var exception = Objects.requireNonNull(result.getException());
                    var error = AutomateBundle.message("general.AutomateCliRunner.Outcome.ThrewException.Message", exception.getMessage());
                    logEntry(error, CliLogEntryType.ERROR);
                    return new CliTextResult(error, "");
                }
                case FailedWithError -> {
                    var error = Objects.requireNonNull(result.getError());
                    logEntry(AutomateBundle.message("general.AutomateCliRunner.UninstallCommand.Outcome.FailedWithError.Message", error), CliLogEntryType.ERROR);
                    return new CliTextResult(error, "");
                }
                default -> throw new RuntimeException(AutomateBundle.message("general.AutomateCliRunner.Outcome.Unknown.Message"));
            }
        }
    }

    @NotNull
    private CliTextResult installCli(@NotNull String currentDirectory) {

        var command = new ArrayList<String>() {{
            add("dotnet");
            add("tool");
            add("install");
            add(AutomateConstants.ExecutableName);
            add("--global");
        }};

        logEntry(AutomateBundle.message("general.AutomateCliRunner.InstallCommand.Started.Message"), CliLogEntryType.NORMAL);

        var result = this.processRunner.start(command, currentDirectory);
        if (result.getSuccess()) {
            var output = Objects.requireNonNull(result.getOutput());
            return new CliTextResult("", output);
        }
        else {
            var cause = Objects.requireNonNull(result.getFailureCause());
            switch (cause) {
                case FailedToStart -> {
                    var error = AutomateBundle.message("general.AutomateCliRunner.InstallCommand.Outcome.FailedToStart.Message");
                    logEntry(error, CliLogEntryType.ERROR);
                    return new CliTextResult(error, "");
                }
                case ThrewException -> {
                    var exception = Objects.requireNonNull(result.getException());
                    var error = AutomateBundle.message("general.AutomateCliRunner.Outcome.ThrewException.Message", exception.getMessage());
                    logEntry(error, CliLogEntryType.ERROR);
                    return new CliTextResult(error, "");
                }
                case FailedWithError -> {
                    var error = Objects.requireNonNull(result.getError());
                    logEntry(AutomateBundle.message("general.AutomateCliRunner.InstallCommand.Outcome.FailedWithError.Message", error), CliLogEntryType.ERROR);
                    return new CliTextResult(error, "");
                }
                default -> throw new RuntimeException(AutomateBundle.message("general.AutomateCliRunner.Outcome.Unknown.Message"));
            }
        }
    }

    @NotNull
    private CliTextResult executeInternal(@NotNull String currentDirectory, @NotNull StringWithDefault executablePath, @NotNull List<String> args, boolean isStructured) {

        var command = new ArrayList<String>();
        command.add(executablePath.getValueOrDefault());
        command.addAll(args);
        if (isStructured) {
            var found = new AtomicBoolean(false);
            AutomateConstants.OutputStructuredAliases.forEach(alias -> {
                if (command.contains(alias)) {
                    found.set(true);
                }
            });
            if (!found.get()) {
                command.add(AutomateConstants.OutputStructuredShorthand);
            }
        }

        logEntry(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Started.Message", String.join(" ", args)), CliLogEntryType.NORMAL);

        var result = this.processRunner.start(command, currentDirectory);
        if (result.getSuccess()) {
            logEntry(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.Success.Message"), CliLogEntryType.SUCCESS);
            var output = Objects.requireNonNull(result.getOutput());
            return new CliTextResult("", output);
        }
        else {
            var cause = Objects.requireNonNull(result.getFailureCause());
            switch (cause) {
                case FailedToStart -> {
                    var error = AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.FailedToStart.Message", executablePath.getValueOrDefault());
                    logEntry(error, CliLogEntryType.ERROR);
                    return new CliTextResult(error, "");
                }
                case ThrewException -> {
                    var exception = Objects.requireNonNull(result.getException());
                    var error = AutomateBundle.message("general.AutomateCliRunner.Outcome.ThrewException.Message", exception.getMessage());
                    logEntry(error, CliLogEntryType.ERROR);
                    return new CliTextResult(error, "");
                }
                case FailedWithError -> {
                    var error = Objects.requireNonNull(result.getError());
                    var message = isStructured
                      ? getStructuredError(error).getErrorMessage()
                      : error;
                    logEntry(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.FailedWithError.Message", message), CliLogEntryType.ERROR);
                    return new CliTextResult(error, "");
                }
                default -> throw new RuntimeException(AutomateBundle.message("general.AutomateCliRunner.Outcome.Unknown.Message"));
            }
        }
    }

    private void logEntry(String text, CliLogEntryType type) {

        var oldValue = new ArrayList<>(this.logs);

        var entry = new CliLogEntry(text, type);
        this.logs.add(entry);

        var newValue = new ArrayList<>();
        newValue.add(entry);

        this.listeners.firePropertyChange(PropertyChanged_Logs, oldValue, newValue);
    }
}
