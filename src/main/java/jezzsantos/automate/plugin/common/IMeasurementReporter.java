package jezzsantos.automate.plugin.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface IMeasurementReporter {

    void enableReporting(@NotNull String machineId, @NotNull String sessionId);

    void measureStartSession(@NotNull String messageTemplate, @Nullable Object... args);

    void measureEndSession(boolean success, @NotNull String messageTemplate, @Nullable Object... args);

    void measureEvent(@NotNull String eventName, @Nullable Map<String, String> context);
}
