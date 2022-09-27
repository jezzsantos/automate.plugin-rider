package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.common.StringWithImplicitDefault;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.lang.module.ModuleDescriptor;
import java.util.List;

public interface IAutomateCliRunner {

    @NotNull
    CliTextResult execute(@NotNull String currentDirectory, @NotNull StringWithImplicitDefault executablePath, @NotNull List<String> args);

    @NotNull <TResult extends StructuredOutput<?>> CliStructuredResult<TResult> executeStructured(@NotNull Class<TResult> outputClass, @NotNull String currentDirectory, @NotNull StringWithImplicitDefault executablePath, @NotNull List<String> args);

    @Nullable
    ModuleDescriptor.Version installLatest(@NotNull String currentDirectory, boolean uninstall);

    @NotNull
    List<CliLogEntry> getLogs();

    void addLogListener(@NotNull PropertyChangeListener listener);

    void removeLogListener(@NotNull PropertyChangeListener listener);

    void log(@NotNull CliLogEntry entry);
}
