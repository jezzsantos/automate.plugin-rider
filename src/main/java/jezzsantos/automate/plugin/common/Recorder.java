package jezzsantos.automate.plugin.common;

import com.intellij.openapi.Disposable;
import com.intellij.serviceContainer.NonInjectable;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.infrastructure.ApplicationInsightsCrashReporter;
import jezzsantos.automate.plugin.infrastructure.ApplicationInsightsMeasurementReporter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Recorder implements IRecorder, Disposable {

    private static final String sessionIdFormat = "rpi_%s";
    private final ICrashReporter crasher;
    private final IMeasurementReporter measurer;
    private final ILogger logger;
    private final ReportingContext reportingContext;

    @UsedImplicitly
    public Recorder() {

        this(new IntelliJLogger(), new ApplicationInsightsCrashReporter(), new ApplicationInsightsMeasurementReporter(), IContainer.getPluginMetadata());
    }

    @TestOnly
    @NonInjectable
    public Recorder(@NotNull ILogger logger, @NotNull ICrashReporter crasher, @NotNull IMeasurementReporter measurer, @NotNull IPluginMetadata pluginMetadata) {

        this.logger = logger;
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
            var sessionId = this.reportingContext.getSessionId();
            var machineId = this.reportingContext.getMachineId();
            this.measurer.enableReporting(machineId, sessionId);
            this.crasher.enableReporting(machineId, sessionId);
        }

        trace(LogLevel.INFORMATION, messageTemplate, args);
        this.measurer.measureStartSession(messageTemplate, args);
    }

    @Override
    public void endSession(boolean success, @NotNull String messageTemplate, @Nullable Object... args) {

        trace(LogLevel.INFORMATION, messageTemplate, args);
        this.measurer.measureEndSession(success, messageTemplate, args);
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
    public <TResult> TResult measureCliCall(@NotNull Supplier<TResult> action, @NotNull String actionName) {

        if (this.reportingContext.getAllowUsage()) {
            return this.measurer.measureCliCall(action, actionName);
        }
        else {
            return action.get();
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

        public String sessionId;
        public String machineId;

        public ReportingIds(@NotNull String machineId, @NotNull String sessionId) {

            this.machineId = machineId;
            this.sessionId = sessionId;
        }

        public String getMachineId() {return this.machineId;}

        public String getSessionId() {return this.sessionId;}
    }
}
