package jezzsantos.automate.plugin.infrastructure;

import com.jetbrains.rd.util.UsedImplicitly;
import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.ExceptionTelemetry;
import com.microsoft.applicationinsights.telemetry.SeverityLevel;
import jezzsantos.automate.ApplicationSettings;
import jezzsantos.automate.plugin.application.services.interfaces.INotifier;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@UsedImplicitly
public class AICrashReportSender implements ICrashReportSender {

    private final TelemetryClient client;

    public AICrashReportSender() {

        this.client = new TelemetryClient();
        var context = this.client.getContext();
        context.setInstrumentationKey(ApplicationSettings.setting("applicationInsightsInstrumentationKey"));
    }

    @Override
    public void send(@NotNull ErrorReport report) {

        var telemetry = new ExceptionTelemetry();
        var cause = getCause(report);
        if (cause != null) {
            telemetry.setException(cause);
        }
        telemetry.setSeverityLevel(SeverityLevel.Critical);
        var properties = telemetry.getProperties();
        properties.put("Device ID", Objects.requireNonNullElse(report.getDeviceId(), AutomateBundle.message("general.AICrashReportSender.UnknownEntry.Message")));
        properties.put("Plugin Version", Objects.requireNonNullElse(report.getVersion(), AutomateBundle.message("general.AICrashReportSender.UnknownEntry.Message")));
        properties.put("Last ActionId", Objects.requireNonNullElse(report.getLastActionId(), AutomateBundle.message("general.AICrashReportSender.EmptyEntry.Message")));
        properties.put("User Comments", Objects.requireNonNullElse(report.getReproSteps(), AutomateBundle.message("general.AICrashReportSender.EmptyEntry.Message")));

        this.client.trackException(telemetry);
        this.client.flush();
    }

    @Nullable
    @Override
    public INotifier.LinkDescriptor getLink() {

        return null;
    }

    @Nullable
    private Throwable getCause(@NotNull ErrorReport report) {

        var exceptions = report.getExceptions();
        if (exceptions.isEmpty()) {
            return null;
        }

        var firstException = exceptions.get(0);
        if (firstException == null) {
            return null;
        }

        var cause = firstException.getCause();
        if (cause == null) {
            return firstException;
        }

        return cause;
    }
}
