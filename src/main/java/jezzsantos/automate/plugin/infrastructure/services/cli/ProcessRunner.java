package jezzsantos.automate.plugin.infrastructure.services.cli;

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
    private static final int MaxWaitForCliToComplete = 30;

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

            var success = process.waitFor(MaxWaitForCliToComplete, TimeUnit.SECONDS);
            if (!success) {
                return ProcessResult.createFailedToStart();
            }
            var stdErr = Objects.requireNonNullElse(stdErrWriter.toString(), "").trim();
            var stdOut = Objects.requireNonNullElse(stdOutWriter.toString(), "").trim();
            var exitCode = process.exitValue();
            if (exitCode != SuccessExitCode) {
                return ProcessResult.createFailedWithError(stdErr, exitCode);
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
