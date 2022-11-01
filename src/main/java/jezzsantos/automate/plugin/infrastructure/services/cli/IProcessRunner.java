package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.intellij.openapi.Disposable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IProcessRunner extends Disposable {

    ProcessResult start(@NotNull List<String> commandLineAndArguments, @NotNull String currentDirectory);
}

class ProcessResult {

    private boolean success;
    private String output;
    private String error;
    private Exception exception;
    private FailureCause cause;

    private Integer exitCode;

    @NotNull
    public static ProcessResult createSuccess(@NotNull String output) {

        var result = new ProcessResult();
        result.success = true;
        result.output = output;
        result.cause = null;
        result.exitCode = null;

        return result;
    }

    @NotNull
    public static ProcessResult createFailedToStart() {

        var result = new ProcessResult();
        result.success = false;
        result.cause = FailureCause.FailedToStart;
        result.exitCode = null;

        return result;
    }

    @NotNull
    public static ProcessResult createFailedWithError(@NotNull String error, int exitCode) {

        var result = new ProcessResult();
        result.success = false;
        result.error = error;
        result.cause = FailureCause.FailedWithError;
        result.exitCode = exitCode;

        return result;
    }

    @NotNull
    public static ProcessResult createFailedWithException(@NotNull Exception e) {

        var result = new ProcessResult();
        result.success = false;
        result.exception = e;
        result.cause = FailureCause.ThrewException;
        result.exitCode = null;

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
    public Integer getExitCode() {

        if (this.success) {
            return null;
        }

        return this.exitCode;
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
