package jezzsantos.automate.plugin.infrastructure.services.cli;

import org.jetbrains.annotations.NotNull;

public class CliTextResult {

    private final String error;
    private final String output;

    public CliTextResult(@NotNull String error, @NotNull String output) {

        this.error = error;
        this.output = output;
    }

    public Boolean isError() {

        return !this.error.isEmpty() && this.output.isEmpty();
    }

    public String getError() {

        return this.error;
    }

    public String getOutput() {

        return this.output;
    }
}


