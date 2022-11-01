package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntryType;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.CliStructuredResult;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.CliTextResult;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredError;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.module.ModuleDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

enum FailureCause {
    FailedToStart,
    ThrewException,
    FailedWithError
}

public class AutomateCliRunner implements IAutomateCliRunner {

    public static final String PropertyChanged_Logs = "CliLogs";
    @NotNull
    private final IProcessRunner processRunner;
    @NotNull
    private final List<CliLogEntry> logs = new ArrayList<>();
    @NotNull
    private final PropertyChangeSupport listeners = new PropertyChangeSupport(this);
    private final IRecorder recorder;

    public AutomateCliRunner() {

        this(IRecorder.getInstance(), new ProcessRunner());
    }

    @TestOnly
    public AutomateCliRunner(@NotNull IRecorder recorder, @NotNull IProcessRunner processRunner) {

        this.recorder = recorder;
        this.processRunner = processRunner;
    }

    @Nullable
    public String getCommandDescriptorFromArgs(List<String> commandLine) {

        if (commandLine == null
          || commandLine.isEmpty()) {
            return null;
        }

        var result = Arrays.stream(Arrays.copyOfRange(commandLine.toArray((new String[0])), 1, 4))
          .filter(Objects::nonNull)
          .filter(arg -> arg.charAt(0) == '@')
          .map(arg -> arg.substring(1))
          .collect(Collectors.joining("."));

        if (result.isEmpty()) {
            return null;
        }

        return result;
    }

    @NotNull
    @Override
    public CliTextResult execute(@NotNull ExecutionContext context, @NotNull List<String> args) {

        return executeInternal(context, args, false);
    }

    @NotNull
    @Override
    public <TResult extends StructuredOutput<?>> CliStructuredResult<TResult> executeStructured(@NotNull Class<TResult> outputClass, @NotNull ExecutionContext context, @NotNull List<String> args) {

        var result = executeInternal(context, args, true);
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
    private static StructuredError getStructuredError(String json) {

        var gson = new Gson();
        try {
            var error = gson.fromJson(json, StructuredError.class);
            if (error != null) {
                return error;
            }
            return new StructuredError(AutomateBundle.message("exception.AutomateCliRunner.StructuredError.Deserialization.Message", json));
        } catch (JsonSyntaxException ex) {
            return new StructuredError(json);
        }
    }

    @NotNull
    private static <TResult> TResult getStructuredOutput(Class<TResult> outputClass, String json) {

        var gson = new Gson();
        return gson.fromJson(json, outputClass);
    }

    @NotNull
    private List<String> tidyCommandLineArgs(@NotNull List<String> commandLine) {

        return commandLine
          .stream()
          .map(chars -> chars.charAt(0) == '@'
            ? chars.substring(1)
            : chars)
          .collect(Collectors.toList());
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

        var commandLine = new ArrayList<String>() {{
            add("dotnet");
            add("@tool");
            add("@uninstall");
            add("@" + AutomateConstants.ExecutableName);
            add("--global");
        }};

        logEntry(AutomateBundle.message("general.AutomateCliRunner.UninstallCommand.Started.Message"), CliLogEntryType.NORMAL);
        return this.recorder.measureCliCall((ignore) -> {
            var result = this.processRunner.start(tidyCommandLineArgs(commandLine), currentDirectory);
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
                        var exitCode = result.getExitCode();
                        logEntry(AutomateBundle.message("general.AutomateCliRunner.UninstallCommand.Outcome.FailedWithError.Message", exitCode, error), CliLogEntryType.ERROR);
                        return new CliTextResult(error, "");
                    }
                    default -> throw new RuntimeException(AutomateBundle.message("general.AutomateCliRunner.Outcome.Unknown.Message"));
                }
            }
        }, "uninstall-cli", getCommandDescriptorFromArgs(commandLine));
    }

    @NotNull
    private CliTextResult installCli(@NotNull String currentDirectory) {

        var commandLine = new ArrayList<String>() {{
            add("dotnet");
            add("@tool");
            add("@install");
            add("@" + AutomateConstants.ExecutableName);
            add("--global");
        }};

        logEntry(AutomateBundle.message("general.AutomateCliRunner.InstallCommand.Started.Message"), CliLogEntryType.NORMAL);

        return this.recorder.measureCliCall((ignore) -> {
            var result = this.processRunner.start(tidyCommandLineArgs(commandLine), currentDirectory);
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
                        var exitCode = result.getExitCode();
                        logEntry(AutomateBundle.message("general.AutomateCliRunner.InstallCommand.Outcome.FailedWithError.Message", exitCode, error), CliLogEntryType.ERROR);
                        return new CliTextResult(error, "");
                    }
                    default -> throw new RuntimeException(AutomateBundle.message("general.AutomateCliRunner.Outcome.Unknown.Message"));
                }
            }
        }, "install-cli", getCommandDescriptorFromArgs(commandLine));
    }

    @NotNull
    private CliTextResult executeInternal(@NotNull ExecutionContext context, @NotNull List<String> args, boolean isStructured) {

        var commandLine = new ArrayList<String>();
        commandLine.add(context.getExecutablePath().getValueOrDefault());
        commandLine.addAll(args);
        if (isStructured) {
            var found = new AtomicBoolean(false);
            AutomateConstants.OutputStructuredOptionAliases.forEach(alias -> {
                if (commandLine.contains(alias)) {
                    found.set(true);
                }
            });
            if (!found.get()) {
                commandLine.add(AutomateConstants.OutputStructuredOptionShorthand);
            }
        }

        return this.recorder.measureCliCall((builder) -> {

            var sessionId = context.getSessionId();
            var correlationId = builder.build(sessionId);

            if (context.allowsUsage()) {
                commandLine.add(AutomateConstants.UsageCorrelationOption);
                commandLine.add(correlationId);
            }
            else {
                commandLine.add(AutomateConstants.UsageAllowedOption);
                commandLine.add("false");
            }

            logEntry(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Started.Message", String.join(" ", tidyCommandLineArgs(args))), CliLogEntryType.NORMAL);

            var result = this.processRunner.start(tidyCommandLineArgs(commandLine), context.getCurrentDirectory());
            if (result.getSuccess()) {
                logEntry(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.Success.Message"), CliLogEntryType.SUCCESS);
                var output = Objects.requireNonNull(result.getOutput());
                return new CliTextResult("", output);
            }
            else {
                var cause = Objects.requireNonNull(result.getFailureCause());
                switch (cause) {
                    case FailedToStart -> {
                        var error = AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.FailedToStart.Message", context.getExecutablePath().getValueOrDefault());
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
                        var error = Objects.requireNonNullElse(result.getError(), "");
                        var exitCode = result.getExitCode();
                        if (error.isEmpty()) {
                            var emptyErrorMessage = AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.FailedWithEmptyError.Message");
                            logEntry(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.FailedWithError.Message", exitCode, emptyErrorMessage),
                                     CliLogEntryType.ERROR);
                            if (isStructured) {
                                var structuredError = new StructuredError(emptyErrorMessage);
                                error = new Gson().toJson(structuredError);
                                return new CliTextResult(error, "");
                            }
                            else {
                                return new CliTextResult(emptyErrorMessage, "");
                            }
                        }
                        else {
                            var message = isStructured
                              ? getStructuredError(error).getErrorMessage()
                              : error;
                            logEntry(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.FailedWithError.Message", exitCode, message), CliLogEntryType.ERROR);
                            return new CliTextResult(error, "");
                        }
                    }
                    default -> throw new RuntimeException(AutomateBundle.message("general.AutomateCliRunner.Outcome.Unknown.Message"));
                }
            }
        }, "instruct-cli", getCommandDescriptorFromArgs(commandLine));
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
