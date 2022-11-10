package jezzsantos.automate.plugin.common.recording;

import com.intellij.serviceContainer.NonInjectable;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.reporting.ICorrelationIdBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class LoggingOnlyRecorder implements IRecorder {

    private static final String sessionIdFormat = "jbrd_ses_%s";
    private final ILogger logger;

    public LoggingOnlyRecorder() {

        this(new IntelliJLogger());
    }

    @NonInjectable
    private LoggingOnlyRecorder(@NotNull ILogger logger) {

        this.logger = logger;
    }

    @Override
    public @NotNull ReportingContext getReportingContext() {

        return new ReportingContext(true, "amachineid", createSessionId());
    }

    @Override
    public void trace(@NotNull LogLevel level, @NotNull String messageTemplate, @Nullable Object... args) {

        Trace(level, null, messageTemplate, args);
    }

    @Override
    public void startSession(boolean enableReporting, @NotNull String messageTemplate, @Nullable Object... args) {

        trace(LogLevel.INFORMATION, messageTemplate, args);
    }

    @Override
    public void endSession(boolean success, @NotNull String messageTemplate, @Nullable Object... args) {

        trace(LogLevel.INFORMATION, messageTemplate, args);
    }

    @Override
    public <TResult> TResult withOperation(@NotNull String operationName, Supplier<TResult> action, @NotNull String startingMessage, @NotNull String endingMessage) {

        boolean success = true;
        try {
            startOperation(operationName, startingMessage);
            return action.get();
        } catch (Exception ex) {
            success = false;
            throw ex;
        } finally {
            endOperation(success, operationName, endingMessage);
        }
    }

    @Override
    public void startOperation(@NotNull String operationName, @NotNull String messageTemplate, @Nullable Object... args) {

        trace(LogLevel.INFORMATION, messageTemplate, args);
    }

    @Override
    public void endOperation(boolean success, @NotNull String operationName, @NotNull String messageTemplate, @Nullable Object... args) {

        trace(LogLevel.INFORMATION, messageTemplate, args);
    }

    @Override
    public void measureEvent(@NotNull String eventName, @Nullable Map<String, String> context) {

        var cleaned = eventName
          .replace(" ", "")
          .toLowerCase();
        var formatted = String.format("jbrd_%s", cleaned);
        trace(LogLevel.INFORMATION, AutomateBundle.message("trace.Recorder.Measure", formatted));
    }

    @Override
    public <TResult> TResult measureCliCall(@NotNull Function<ICorrelationIdBuilder, TResult> action, @NotNull String actionName, @Nullable String command) {

        return action.apply(null);
    }

    @Override
    public void crash(@NotNull CrashLevel level, @NotNull Throwable exception, @NotNull String messageTemplate, @Nullable Object... args) {

        Trace(LogLevel.ERROR, exception, AutomateBundle.message("trace.Recorder.Crash", messageTemplate), args);
    }

    @Override
    public void crash(@NotNull CrashLevel level, @NotNull Throwable exception, @Nullable Map<String, String> additionalProperties, @NotNull String messageTemplate, @Nullable Object... args) {

        Trace(LogLevel.ERROR, exception, AutomateBundle.message("trace.Recorder.Crash", messageTemplate), args);
    }

    @Override
    public void dispose() {

    }

    private String createSessionId() {

        return String.format(sessionIdFormat, UUID.randomUUID().toString().replace("-", "").toLowerCase());
    }

    private void Trace(@NotNull LogLevel level, @Nullable Throwable exception, @NotNull String messageTemplate, @Nullable Object... args) {

        this.logger.log(level, exception, messageTemplate, args);
    }
}
