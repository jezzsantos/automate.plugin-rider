package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.common.recording.LogLevel;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ProcessRunner implements IProcessRunner {

    private static final int SuccessExitCode = 0;
    private static final int MaxWaitForCliToCompleteInSecs = 30;
    private final IRecorder recorder;

    public ProcessRunner(IRecorder recorder) {

        this.recorder = recorder;
    }

    @Override
    public void dispose() {

    }

    @Override
    public ProcessResult start(@NotNull List<String> commandLineAndArguments, @NotNull String currentDirectory) {

        //PROBLEM: On MacOS, we are executing the `automate` CLI, which in turn executes the 'dotnet' command, which it cannot find
        // we think, because the PATH variable is not set (or not used by automate) to run the sub-command.
        var builder = new ProcessBuilder(commandLineAndArguments);
        builder.redirectErrorStream(false);

        builder.directory(new File(currentDirectory));
        this.recorder.trace(LogLevel.INFORMATION, AutomateBundle.message("trace.ProcessRunner.Start.Before", currentDirectory, String.join(" ", commandLineAndArguments)));

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

            var success = process.waitFor(MaxWaitForCliToCompleteInSecs, TimeUnit.SECONDS);
            if (!success) {
                this.recorder.trace(LogLevel.ERROR, AutomateBundle.message("trace.ProcessRunner.After.FailedToStart", MaxWaitForCliToCompleteInSecs));
                return ProcessResult.createFailedToStart();
            }
            var stdErr = Objects.requireNonNullElse(stdErrWriter.toString(), "").trim();
            var stdOut = Objects.requireNonNullElse(stdOutWriter.toString(), "").trim();
            var exitCode = process.exitValue();
            if (exitCode != SuccessExitCode) {
                this.recorder.trace(LogLevel.ERROR,
                                    AutomateBundle.message("trace.ProcessRunner.After.FailedWithError", exitCode, stdOut, stdErr));
                return ProcessResult.createFailedWithError(stdErr, exitCode);
            }
            else {
                this.recorder.trace(LogLevel.INFORMATION,
                                    AutomateBundle.message("trace.ProcessRunner.After.Success", exitCode, stdOut, stdErr));
                return ProcessResult.createSuccess(stdOut);
            }
        } catch (InterruptedException | IOException exception) {
            this.recorder.trace(LogLevel.ERROR,
                                AutomateBundle.message("trace.ProcessRunner.After.FailedWithException", Objects.requireNonNullElse(exception.getCause(), exception).toString()));
            return ProcessResult.createFailedWithException(exception);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}
