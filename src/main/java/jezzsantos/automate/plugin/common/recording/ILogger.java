package jezzsantos.automate.plugin.common.recording;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ILogger {

    void log(@NotNull LogLevel level, @Nullable Throwable exception, @NotNull String messageTemplate, @Nullable Object... args);
}
