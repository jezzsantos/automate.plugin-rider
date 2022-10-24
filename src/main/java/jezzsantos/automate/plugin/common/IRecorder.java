package jezzsantos.automate.plugin.common;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

enum LogLevel {
    TRACE,
    DEBUG,
    INFORMATION,
    WARNING,
    ERROR,
    CRITICAL,
    NONE
}

public interface IRecorder extends Disposable {

    static IRecorder getInstance() {

        return ApplicationManager.getApplication().getService(IRecorder.class);
    }

    @NotNull
    ReportingContext getReportingContext();

    void trace(@NotNull LogLevel level, @NotNull String messageTemplate, @Nullable Object... args);

    void startSession(boolean enableReporting, @NotNull String messageTemplate, @Nullable Object... args);

    void endSession(boolean success, @NotNull String messageTemplate, @Nullable Object... args);

    void measureEvent(@NotNull String eventName, @Nullable Map<String, String> context);

    void crash(@NotNull CrashLevel level, @NotNull Throwable exception, @NotNull String messageTemplate, @Nullable Object... args);

    void crash(@NotNull CrashLevel level, @NotNull Throwable exception, @Nullable Map<String, String> additionalProperties, @NotNull String messageTemplate, @Nullable Object... args);
}

