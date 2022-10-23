package jezzsantos.automate.plugin.infrastructure.services.cli.responses;

import org.jetbrains.annotations.Nullable;

public class CliStructuredResult<TResult> {

    private final StructuredError error;
    private final TResult output;

    public CliStructuredResult(@Nullable StructuredError error, @Nullable TResult output) {

        this.error = error;
        this.output = output;
    }

    public Boolean isError() {

        return this.error != null;
    }

    public StructuredError getError() {

        return this.error;
    }

    public TResult getOutput() {

        return this.output;
    }
}
