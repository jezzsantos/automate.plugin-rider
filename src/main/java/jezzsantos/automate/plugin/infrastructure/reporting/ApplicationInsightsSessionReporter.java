package jezzsantos.automate.plugin.infrastructure.reporting;

import com.intellij.openapi.Disposable;
import com.microsoft.applicationinsights.telemetry.Duration;
import com.microsoft.applicationinsights.telemetry.RequestTelemetry;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.ILogger;
import jezzsantos.automate.plugin.common.recording.ISessionReporter;
import jezzsantos.automate.plugin.common.recording.LogLevel;
import org.apache.commons.lang.time.StopWatch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.time.Instant;
import java.util.*;

public class ApplicationInsightsSessionReporter implements ISessionReporter, Disposable {

    public static final String SessionName = "session";
    private final ITelemetryClient client;
    private final ILogger logger;
    private final Operations operations;
    private boolean reportingEnabled;

    public ApplicationInsightsSessionReporter(@NotNull ILogger logger, @NotNull ITelemetryClient telemetryClient) {

        this.logger = logger;
        this.client = telemetryClient;
        this.reportingEnabled = false;
        this.operations = new Operations();
    }

    @TestOnly
    public Operations getOperations() {return this.operations;}

    @Override
    public void enableReporting(@NotNull String machineId, @NotNull String sessionId) {

        this.reportingEnabled = true;

        this.client.setRoleInstance(machineId);
        this.client.setDeviceId(machineId);
        this.client.setUserId(machineId);
        this.client.setSessionId(sessionId);
    }

    @Override
    public void measureStartSession() {

        measureStartOperation(SessionName);
    }

    @Override
    public void measureEndSession(boolean success) {

        measureEndOperation(SessionName, success);

        if (!this.operations.isEmpty()) {
            var reversedOperations = this.operations.inReverse();
            reversedOperations
              .forEach(operation -> Try.safely(() -> measureEndOperation(operation.getName(), true)));
        }

        if (this.reportingEnabled) {
            this.logger.log(LogLevel.DEBUG, null, AutomateBundle.message("trace.ApplicationInsightsMeasurementReporter.Flushing.Message"));
            this.client.sendAllTelemetry();
        }
    }

    @Override
    public void measureStartOperation(@NotNull String operationName) {

        var parent = this.operations.getCurrent();
        var child = new RunningOperation(operationName, parent);
        this.operations.push(child);

        this.client.setOperationId(child.getId());
    }

    @Override
    public void measureEndOperation(@NotNull String operationName, boolean success) {

        var current = this.operations.getCurrent();
        if (current == null
          || !current.getName().equals(operationName)) {
            return;
        }

        current.stop();

        if (this.reportingEnabled) {
            var telemetry = current.createTelemetry(success);
            this.client.trackRequest(telemetry);
        }

        this.operations.pop();

        var parent = this.operations.getCurrent();
        this.client.setOperationId(parent != null
                                     ? parent.getId()
                                     : null);
    }

    @Override
    public void dispose() {

        if (this.client instanceof Disposable disposable) {
            disposable.dispose();
        }
    }

    static class Operations {

        private final List<RunningOperation> operationsStack;

        public Operations() {

            this.operationsStack = new ArrayList<>();
        }

        @Nullable
        public ApplicationInsightsSessionReporter.RunningOperation getCurrent() {

            if (this.operationsStack.isEmpty()) {
                return null;
            }

            var lastIndex = this.operationsStack.size() - 1;
            return this.operationsStack.get(lastIndex);
        }

        public boolean isEmpty() {return this.operationsStack.isEmpty();}

        @NotNull
        public List<RunningOperation> inReverse() {

            var reversedOperations = new ArrayList<>(this.operationsStack);
            Collections.reverse(reversedOperations);
            return reversedOperations;
        }

        public void push(@NotNull ApplicationInsightsSessionReporter.RunningOperation next) {

            this.operationsStack.add(next);
        }

        public void pop() {

            if (this.operationsStack.isEmpty()) {
                return;
            }

            this.operationsStack.remove(this.operationsStack.size() - 1);
        }
    }

    static class RunningOperation {

        private final String id;
        private final RequestTelemetry telemetry;
        private final StopWatch stopwatch;
        private final RunningOperation parent;
        private final String name;

        public RunningOperation(@NotNull String name, @Nullable ApplicationInsightsSessionReporter.RunningOperation parent) {

            this.id = createOperationId();
            this.name = name;
            this.parent = parent;
            this.telemetry = new RequestTelemetry();
            this.stopwatch = new StopWatch();
            initTelemetry();
        }

        @NotNull
        public String getId() {return this.id;}

        @NotNull
        public String getName() {return this.name;}

        public void stop() {

            this.stopwatch.stop();
        }

        public RequestTelemetry createTelemetry(boolean success) {

            var duration = new Duration(this.stopwatch.getTime());
            this.telemetry.setSuccess(success);
            this.telemetry.setDuration(duration);

            return this.telemetry;
        }

        public RunningOperation getParent() {return this.parent;}

        private void initTelemetry() {

            var parentId = this.parent != null
              ? this.parent.getId()
              : null;

            this.stopwatch.start();
            this.telemetry.setName(formatName(this.name));
            this.telemetry.setId(this.id);
            this.telemetry.setTimestamp(Date.from(Instant.now()));
            this.telemetry.getContext().getOperation().setParentId(parentId);
        }

        @NotNull
        private String createOperationId() {

            return String.format("jbrd_opr_%s", UUID.randomUUID().toString().replace("-", ""));
        }

        @SuppressWarnings("SameParameterValue")
        @NotNull
        private String formatName(@NotNull String name) {

            return String.format("jbrd-operation-%s", name.toLowerCase());
        }
    }
}
