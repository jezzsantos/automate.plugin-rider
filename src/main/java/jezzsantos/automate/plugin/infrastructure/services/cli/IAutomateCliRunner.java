package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface IAutomateCliRunner {

    @NotNull
    CliTextResult execute(@NotNull String executablePath, @NotNull List<String> args);

    @NotNull <TResult extends StructuredOutput<?>> CliStructuredResult<TResult> executeStructured(@NotNull Class<TResult> outputClass, @NotNull String executablePath, @NotNull List<String> args);

    @NotNull
    List<CliLogEntry> getLogs();

    void addLogListener(@NotNull PropertyChangeListener listener);

    void removeLogListener(@NotNull PropertyChangeListener listener);

    void log(@NotNull CliLogEntry entry);
}
