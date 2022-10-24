package jezzsantos.automate.plugin.infrastructure.reporting;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.Duration;
import com.microsoft.applicationinsights.telemetry.EventTelemetry;
import com.microsoft.applicationinsights.telemetry.RemoteDependencyTelemetry;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.common.recording.IMeasurementReporter;
import jezzsantos.automate.plugin.infrastructure.ui.ApplicationInsightsClient;
import org.apache.commons.lang.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

public class ApplicationInsightsMeasurementReporter implements IMeasurementReporter {

    private final TelemetryClient client;
    private boolean reportingEnabled;

    public ApplicationInsightsMeasurementReporter() {

        this.client = ApplicationInsightsClient.getClient();
        this.reportingEnabled = false;
    }

    @Override
    public void enableReporting(@NotNull String machineId, @NotNull String sessionId) {

        this.reportingEnabled = true;
    }

    @Override
    public void measureEvent(@NotNull String eventName, @Nullable Map<String, String> context) {

        if (this.reportingEnabled) {
            var telemetry = new EventTelemetry();
            telemetry.setName(eventName.toLowerCase());
            if (context != null) {
                telemetry.getProperties().putAll(context);
            }

            this.client.trackEvent(telemetry);
        }
    }

    @Override
    public <TResult> TResult measureCliCall(@NotNull Function<ICorrelationIdBuilder, TResult> action, @NotNull String actionName, @Nullable String command) {

        if (this.reportingEnabled) {

            var dependencyTelemetry = new RemoteDependencyTelemetry(actionName);
            dependencyTelemetry.setCommandName(command != null
                                                 ? command
                                                 : "<empty>");
            var dependencyId = createDependencyId();
            dependencyTelemetry.setId(dependencyId);
            dependencyTelemetry.setType("CLI command");
            dependencyTelemetry.setTimestamp(Date.from(Instant.now()));
            dependencyTelemetry.setTarget(AutomateConstants.ApplicationInsightsCliRoleName);
            dependencyTelemetry.setType("commandline");

            boolean success = false;
            var stopWatch = new StopWatch();
            stopWatch.start();
            try {

                var operationId = Objects.requireNonNullElse(this.client.getContext().getOperation().getId(), createOperationId());
                var builder = new CorrelationIdBuilder(dependencyId, operationId);
                var result = action.apply(builder);
                success = true;

                return result;
            } finally {
                stopWatch.stop();
                var duration = new Duration(stopWatch.getTime());
                dependencyTelemetry.setSuccess(success);
                dependencyTelemetry.setDuration(duration);

                this.client.trackDependency(dependencyTelemetry);
            }
        }
        else {
            return action.apply(null);
        }
    }

    @NotNull
    private String createDependencyId() {

        return String.format("jbrd_dep_%s", UUID.randomUUID().toString().replace("-", ""));
    }

    @NotNull
    private String createOperationId() {

        return String.format("jbrd_opr_%s", UUID.randomUUID().toString().replace("-", ""));
    }

    private static class CorrelationIdBuilder implements ICorrelationIdBuilder {

        private final String dependencyId;
        private final String operationId;

        public CorrelationIdBuilder(@NotNull String dependencyId, @NotNull String operationId) {

            this.dependencyId = dependencyId;
            this.operationId = operationId;
        }

        @NotNull
        public String build(@NotNull String sessionId) {

            return createCorrelationId(this.dependencyId, this.operationId, sessionId);
        }

        @NotNull
        private static String createCorrelationId(@NotNull String dependencyId, @NotNull String operationId, @NotNull String sessionId) {

            return String.format("%s|%s|%s", sessionId, operationId, dependencyId);
        }
    }
}
