package jezzsantos.automate.plugin.infrastructure;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.services.interfaces.INotifier;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.CrashLevel;
import jezzsantos.automate.plugin.common.IRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

@UsedImplicitly
public class ApplicationInsightsCrashReportSender implements ICrashReportSender {

    @Override
    public void send(@NotNull ErrorReport report) {

        var cause = getCause(report);
        IRecorder.getInstance().crash(CrashLevel.FATAL, cause, Map.of(
          "Plugin Version", Objects.requireNonNullElse(report.getVersion(), AutomateBundle.message("general.AICrashReportSender.UnknownEntry.Message")),
          "Last ActionId", Objects.requireNonNullElse(report.getLastActionId(), AutomateBundle.message("general.AICrashReportSender.EmptyEntry.Message")),
          "User Comments", Objects.requireNonNullElse(report.getReproSteps(), AutomateBundle.message("general.AICrashReportSender.EmptyEntry.Message"))
        ), cause.getMessage());
    }

    @Nullable
    @Override
    public INotifier.LinkDescriptor getLink() {

        return null;
    }

    @NotNull
    private Throwable getCause(@NotNull ErrorReport report) {

        var exceptions = report.getExceptions();
        if (exceptions.isEmpty()) {
            return new Exception("Unexpected exception");
        }

        var firstException = exceptions.get(0);
        if (firstException == null) {
            return new Exception("Unexpected exception");
        }

        var cause = firstException.getCause();
        if (cause == null) {
            return firstException;
        }

        return cause;
    }
}
