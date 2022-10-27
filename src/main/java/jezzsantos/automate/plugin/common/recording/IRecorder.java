package jezzsantos.automate.plugin.common.recording;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import jezzsantos.automate.plugin.infrastructure.reporting.ICorrelationIdBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public interface IRecorder extends Disposable {

    static IRecorder getInstance() {

        return ApplicationManager.getApplication().getService(IRecorder.class);
    }

    @NotNull
    ReportingContext getReportingContext();

    void trace(@NotNull LogLevel level, @NotNull String messageTemplate, @Nullable Object... args);

    void startSession(boolean enableReporting, @NotNull String messageTemplate, @Nullable Object... args);

    void endSession(boolean success, @NotNull String messageTemplate, @Nullable Object... args);

    <TResult> TResult withOperation(@NotNull String operationName, Supplier<TResult> action, @NotNull String startingMessage, @NotNull String endingMessage);

    void startOperation(@NotNull String operationName, @NotNull String messageTemplate, @Nullable Object... args);

    void endOperation(boolean success, @NotNull String operationName, @NotNull String messageTemplate, @Nullable Object... args);

    void measureEvent(@NotNull String eventName, @Nullable Map<String, String> context);

    <TResult> TResult measureCliCall(@NotNull Function<ICorrelationIdBuilder, TResult> action, @NotNull String actionName, @Nullable String command);

    void crash(@NotNull CrashLevel level, @NotNull Throwable exception, @NotNull String messageTemplate, @Nullable Object... args);

    void crash(@NotNull CrashLevel level, @NotNull Throwable exception, @Nullable Map<String, String> additionalProperties, @NotNull String messageTemplate, @Nullable Object... args);
}

