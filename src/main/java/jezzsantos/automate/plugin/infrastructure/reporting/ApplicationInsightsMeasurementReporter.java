package jezzsantos.automate.plugin.infrastructure.reporting;

import com.microsoft.applicationinsights.telemetry.Duration;
import com.microsoft.applicationinsights.telemetry.EventTelemetry;
import com.microsoft.applicationinsights.telemetry.RemoteDependencyTelemetry;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.common.recording.IMeasurementReporter;
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

    private final ITelemetryClient client;
    private boolean reportingEnabled;

    public ApplicationInsightsMeasurementReporter(@NotNull ITelemetryClient telemetryClient) {

        this.client = telemetryClient;
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
            telemetry.getContext().getOperation().setParentId(this.client.getOperationId());

            this.client.trackEvent(telemetry);
        }
    }

    @Override
    public <TResult> TResult measureCliCall(@NotNull Function<ICorrelationIdBuilder, TResult> action, @NotNull String actionName, @Nullable String command) {

        if (this.reportingEnabled) {

            var telemetry = new RemoteDependencyTelemetry(actionName);
            telemetry.setCommandName(command != null
                                       ? command
                                       : "<empty>");
            var dependencyId = createDependencyId();
            telemetry.setId(dependencyId);
            telemetry.setTimestamp(Date.from(Instant.now()));
            telemetry.setTarget(AutomateConstants.ApplicationInsightsCliRoleName);
            telemetry.setType("commandline");
            telemetry.getContext().getOperation().setParentId(this.client.getOperationId());

            boolean success = false;
            var stopWatch = new StopWatch();
            stopWatch.start();
            try {

                var operationId = Objects.requireNonNullElse(this.client.getOperationId(), createOperationId());
                var builder = new CorrelationIdBuilder(operationId, dependencyId);
                var result = action.apply(builder);
                success = true;

                return result;
            } finally {
                stopWatch.stop();
                var duration = new Duration(stopWatch.getTime());
                telemetry.setSuccess(success);
                telemetry.setDuration(duration);

                this.client.trackDependency(telemetry);
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

    public static class CorrelationIdBuilder implements ICorrelationIdBuilder {

        private final String dependencyId;
        private final String operationId;

        public CorrelationIdBuilder(@NotNull String operationId, @NotNull String dependencyId) {

            this.operationId = operationId;
            this.dependencyId = dependencyId;
        }

        @NotNull
        public String build(@NotNull String sessionId) {

            return createCorrelationId(sessionId, this.operationId, this.dependencyId);
        }

        @NotNull
        private static String createCorrelationId(@NotNull String sessionId, @NotNull String operationId, @NotNull String dependencyId) {

            return String.format("%s|%s|%s", sessionId, operationId, dependencyId);
        }
    }
}
