package jezzsantos.automate.plugin.common.recording;

import com.intellij.openapi.Disposable;
import com.intellij.serviceContainer.NonInjectable;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.IContainer;
import jezzsantos.automate.plugin.common.IPluginMetadata;
import jezzsantos.automate.plugin.infrastructure.reporting.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Recorder implements IRecorder, Disposable {

    private static final String sessionIdFormat = "jbrd_ses_%s";
    private final ICrashReporter crasher;
    private final IMeasurementReporter measurer;
    private final ISessionReporter sessioner;
    private final ILogger logger;
    private final ReportingContext reportingContext;

    @UsedImplicitly
    public Recorder() {

        this(new IntelliJLogger());
    }

    @NonInjectable
    private Recorder(@NotNull ILogger logger) {

        this(logger, ITelemetryClient.getInstance(), IContainer.getPluginMetadata());
    }

    @NonInjectable
    private Recorder(@NotNull ILogger logger, @NotNull ITelemetryClient telemetryClient, @NotNull IPluginMetadata pluginMetadata) {

        this(logger, new ApplicationInsightsSessionReporter(logger, telemetryClient), new ApplicationInsightsCrashReporter(telemetryClient),
             new ApplicationInsightsMeasurementReporter(telemetryClient),
             pluginMetadata);
    }

    @TestOnly
    @NonInjectable
    public Recorder(@NotNull ILogger logger, @NotNull ISessionReporter sessioner, @NotNull ICrashReporter crasher, @NotNull IMeasurementReporter measurer, @NotNull IPluginMetadata pluginMetadata) {

        this.logger = logger;
        this.sessioner = sessioner;
        this.crasher = crasher;
        this.measurer = measurer;
        this.reportingContext = new ReportingContext(false, pluginMetadata.getInstallationId(), createSessionId());
    }

    @Override
    public void dispose() {

        if (this.crasher != null) {
            if (this.crasher instanceof Disposable disposable) {
                disposable.dispose();
            }
        }
        if (this.measurer != null) {
            if (this.measurer instanceof Disposable disposable) {
                disposable.dispose();
            }
        }
        if (this.sessioner != null) {
            if (this.sessioner instanceof Disposable disposable) {
                disposable.dispose();
            }
        }
    }

    @Override
    public @NotNull ReportingContext getReportingContext() {

        return this.reportingContext;
    }

    @Override
    public void trace(@NotNull LogLevel level, @NotNull String messageTemplate, @Nullable Object... args) {

        Trace(level, null, messageTemplate, args);
    }

    @Override
    public void startSession(boolean allowUsage, @NotNull String messageTemplate, @Nullable Object... args) {

        this.reportingContext.setAllowUsage(allowUsage);

        if (allowUsage) {
            var session = this.reportingContext.getSessionId();
            var machineId = this.reportingContext.getMachineId();
            this.sessioner.enableReporting(machineId, session);
            this.measurer.enableReporting(machineId, session);
            this.crasher.enableReporting(machineId, session);
        }

        trace(LogLevel.INFORMATION, messageTemplate, args);
        this.sessioner.measureStartSession();
    }

    @Override
    public void endSession(boolean success, @NotNull String messageTemplate, @Nullable Object... args) {

        trace(LogLevel.INFORMATION, messageTemplate, args);
        this.sessioner.measureEndSession(success);
    }

    @Override
    public <TResult> TResult withOperation(@NotNull String operationName, Supplier<TResult> action, @NotNull String startingMessage, @NotNull String endingMessage) {

        boolean success = true;
        try {
            startOperation(operationName, startingMessage);
            return action.get();
        } catch (Exception ex) {
            success = false;
            throw ex;
        } finally {
            endOperation(success, operationName, endingMessage);
        }
    }

    @Override
    public void startOperation(@NotNull String operationName, @NotNull String messageTemplate, @Nullable Object... args) {

        trace(LogLevel.INFORMATION, messageTemplate, args);
        this.sessioner.measureStartOperation(operationName);
    }

    @Override
    public void endOperation(boolean success, @NotNull String operationName, @NotNull String messageTemplate, @Nullable Object... args) {

        trace(LogLevel.INFORMATION, messageTemplate, args);
        this.sessioner.measureEndOperation(operationName, success);
    }

    @Override
    public void measureEvent(@NotNull String eventName, @Nullable Map<String, String> context) {

        var cleaned = eventName
          .replace(" ", "")
          .toLowerCase();
        var formatted = String.format("jbrd_%s", cleaned);
        trace(LogLevel.INFORMATION, AutomateBundle.message("trace.Recorder.Measure", formatted));
        if (this.reportingContext.getAllowUsage()) {
            this.measurer.measureEvent(formatted, context);
        }
    }

    @Override
    public <TResult> TResult measureCliCall(@NotNull Function<ICorrelationIdBuilder, TResult> action, @NotNull String actionName, @Nullable String command) {

        if (this.reportingContext.getAllowUsage()) {
            return this.measurer.measureCliCall(action, actionName, command);
        }
        else {
            return action.apply(new ApplicationInsightsMeasurementReporter.SessionOnlyCorrelationBuilder());
        }
    }

    @Override
    public void crash(@NotNull CrashLevel level, @NotNull Throwable exception, @NotNull String messageTemplate, @Nullable Object... args) {

        crash(level, exception, null, messageTemplate, args);
    }

    @Override
    public void crash(@NotNull CrashLevel level, @NotNull Throwable exception, @Nullable Map<String, String> additionalProperties, @NotNull String messageTemplate, @Nullable Object... args) {

        Trace(LogLevel.ERROR, exception, AutomateBundle.message("trace.Recorder.Crash", messageTemplate), args);
        if (this.reportingContext.getAllowUsage()) {
            this.crasher.crash(level, exception, additionalProperties, messageTemplate, args);
        }
    }

    private void Trace(@NotNull LogLevel level, @Nullable Throwable exception, @NotNull String messageTemplate, @Nullable Object... args) {

        this.logger.log(level, exception, messageTemplate, args);
    }

    private String createSessionId() {

        return String.format(sessionIdFormat, UUID.randomUUID().toString().replace("-", "").toLowerCase());
    }

    static class ReportingIds {

        public String requestId;
        public String machineId;

        public ReportingIds(@NotNull String machineId, @NotNull String requestId) {

            this.machineId = machineId;
            this.requestId = requestId;
        }

        public String getMachineId() {return this.machineId;}

        public String getRequestId() {return this.requestId;}
    }
}
