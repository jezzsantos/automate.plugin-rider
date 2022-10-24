package jezzsantos.automate.plugin.infrastructure.reporting;

import com.intellij.openapi.Disposable;
import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.Duration;
import com.microsoft.applicationinsights.telemetry.RequestTelemetry;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.recording.ILogger;
import jezzsantos.automate.plugin.common.recording.ISessionReporter;
import jezzsantos.automate.plugin.common.recording.LogLevel;
import jezzsantos.automate.plugin.infrastructure.ui.ApplicationInsightsClient;
import org.apache.commons.lang.time.StopWatch;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ApplicationInsightsSessionReporter implements ISessionReporter, Disposable {

    private static final String SessionName = "use";
    private final TelemetryClient client;
    private final ILogger logger;
    private final Map<String, RunningOperationContext> operations;
    private boolean reportingEnabled;

    public ApplicationInsightsSessionReporter(@NotNull ILogger logger) {

        this.logger = logger;
        this.client = ApplicationInsightsClient.getClient();
        this.reportingEnabled = false;
        this.operations = new HashMap<>();
    }

    @Override
    public void enableReporting(@NotNull String machineId, @NotNull String sessionId) {

        this.reportingEnabled = true;
        var context = this.client.getContext();
        context.getCloud().setRoleInstance(machineId);
        context.getDevice().setId(machineId);
        context.getUser().setId(machineId);
        context.getSession().setId(sessionId);
    }

    @Override
    public void measureStartSession() {

        measureStartOperation(getOperationName(SessionName));
    }

    @Override
    public void measureEndSession(boolean success) {

        measureEndOperation(getOperationName(SessionName), success);

        this.logger.log(LogLevel.DEBUG, null, AutomateBundle.message("trace.ApplicationInsightsMeasurementReporter.Flushing.Message"));
        this.client.flush();
    }

    @Override
    public void measureStartOperation(@NotNull String operationName) {

        var operation = new RunningOperationContext(operationName);
        this.operations.put(operationName, operation);

        this.client.getContext().getOperation().setId(operation.getId());
        this.client.getContext().getOperation().setParentId(operation.getId());
    }

    @Override
    public void measureEndOperation(@NotNull String operationName, boolean success) {

        if (!this.operations.containsKey(operationName)) {
            return;
        }

        var operation = this.operations.get(operationName);
        operation.stop();

        if (this.reportingEnabled) {
            var telemetry = operation.getTelemetry(success);
            this.client.trackRequest(telemetry);
        }

        this.operations.remove(operationName);
    }

    @Override
    public void dispose() {

        if (this.client != null) {
            this.client.flush();
        }
    }

    @SuppressWarnings("SameParameterValue")
    @NotNull
    private String getOperationName(@NotNull String name) {

        return String.format("jbrd-plugin-%s", name);
    }

    static class RunningOperationContext {

        private final String id;
        private final RequestTelemetry telemetry;
        private final StopWatch stopwatch;

        public RunningOperationContext(@NotNull String name) {

            this.id = createOperationId();
            this.telemetry = new RequestTelemetry();
            this.stopwatch = new StopWatch();
            initTelemetry(name);
        }

        @NotNull
        public String getId() {return this.id;}

        public void stop() {

            this.stopwatch.stop();
        }

        public RequestTelemetry getTelemetry(boolean success) {

            var duration = new Duration(this.stopwatch.getTime());
            this.telemetry.setSuccess(success);
            this.telemetry.setDuration(duration);

            return this.telemetry;
        }

        private void initTelemetry(String name) {

            this.stopwatch.start();
            this.telemetry.setName(name);
            this.telemetry.setId(this.id);
            this.telemetry.setTimestamp(Date.from(Instant.now()));
        }

        @NotNull
        private String createOperationId() {

            return String.format("jbrd_opr_%s", UUID.randomUUID().toString().replace("-", ""));
        }
    }
}
