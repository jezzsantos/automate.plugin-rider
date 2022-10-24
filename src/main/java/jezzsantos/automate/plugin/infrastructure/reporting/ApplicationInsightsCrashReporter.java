package jezzsantos.automate.plugin.infrastructure.reporting;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.ExceptionTelemetry;
import com.microsoft.applicationinsights.telemetry.SeverityLevel;
import jezzsantos.automate.plugin.common.recording.CrashLevel;
import jezzsantos.automate.plugin.common.recording.ICrashReporter;
import jezzsantos.automate.plugin.infrastructure.ui.ApplicationInsightsClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ApplicationInsightsCrashReporter implements ICrashReporter {

    private final TelemetryClient client;
    private boolean reportingEnabled;

    public ApplicationInsightsCrashReporter() {

        this.client = ApplicationInsightsClient.getClient();
        this.reportingEnabled = false;
    }

    @Override
    public void enableReporting(@NotNull String machineId, @NotNull String sessionId) {

        this.reportingEnabled = true;
    }

    @Override
    public void crash(@NotNull CrashLevel level, @NotNull Throwable exception, @NotNull String messageTemplate, @Nullable Object... args) {

        crash(level, exception, null, messageTemplate, args);
    }

    @Override
    public void crash(@NotNull CrashLevel level, @NotNull Throwable exception, @Nullable Map<String, String> additionalProperties, @NotNull String messageTemplate, @Nullable Object... args) {

        if (this.reportingEnabled) {

            var argsString = args != null
              ? Arrays.stream(args)
              .filter(Objects::nonNull)
              .map(Object::toString)
              .collect(Collectors.joining(", "))
              : "";

            var telemetry = new ExceptionTelemetry();
            telemetry.setException(exception);
            telemetry.setSeverityLevel(getSeverityFromLevel(level));
            if (additionalProperties != null) {
                telemetry.getProperties().putAll(additionalProperties);
            }
            telemetry.getProperties().put("Message_Template", messageTemplate);
            telemetry.getProperties().put("Message_Arguments", argsString);

            this.client.trackException(telemetry);
        }
    }

    private SeverityLevel getSeverityFromLevel(CrashLevel level) {

        if (level == CrashLevel.FATAL) {
            return SeverityLevel.Critical;
        }

        return SeverityLevel.Error;
    }
}
