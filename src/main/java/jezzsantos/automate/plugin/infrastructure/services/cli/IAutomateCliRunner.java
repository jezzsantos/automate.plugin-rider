package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.common.StringWithDefault;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.CliStructuredResult;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.CliTextResult;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.lang.module.ModuleDescriptor;
import java.util.List;

public interface IAutomateCliRunner {

    @NotNull
    CliTextResult execute(@NotNull ExecutionContext context, @NotNull List<String> args);

    @NotNull <TResult extends StructuredOutput<?>> CliStructuredResult<TResult> executeStructured(@NotNull Class<TResult> outputClass, @NotNull ExecutionContext context, @NotNull List<String> args);

    @Nullable
    ModuleDescriptor.Version installLatest(boolean uninstall);

    @NotNull
    List<CliLogEntry> getLogs();

    void addLogListener(@NotNull PropertyChangeListener listener);

    void removeLogListener(@NotNull PropertyChangeListener listener);

    void log(@NotNull CliLogEntry entry);
}

class ExecutionContext {

    private final String currentDirectory;
    private final StringWithDefault executablePath;
    private final boolean allowUsage;
    private final String sessionId;

    public ExecutionContext(@NotNull String currentDirectory, @NotNull StringWithDefault executablePath, boolean allowUsage, @NotNull String sessionId) {

        this.currentDirectory = currentDirectory;
        this.executablePath = executablePath;
        this.allowUsage = allowUsage;
        this.sessionId = sessionId;
    }

    public StringWithDefault getExecutablePath() {return this.executablePath;}

    public boolean allowsUsage() {return this.allowUsage;}

    public String getSessionId() {return this.sessionId;}

    public String getCurrentDirectory() {return this.currentDirectory;}
}
