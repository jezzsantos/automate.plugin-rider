package jezzsantos.automate.plugin.common.recording;

import jezzsantos.automate.plugin.infrastructure.reporting.ICorrelationIdBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public interface IMeasurementReporter {

    void enableReporting(@NotNull String machineId, @NotNull String sessionId);

    void measureEvent(@NotNull String eventName, @Nullable Map<String, String> context);

    <TResult> TResult measureCliCall(@NotNull Function<ICorrelationIdBuilder, TResult> action, @NotNull String actionName, @Nullable String command);
}
