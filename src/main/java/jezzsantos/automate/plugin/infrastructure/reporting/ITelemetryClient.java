package jezzsantos.automate.plugin.infrastructure.reporting;

import com.microsoft.applicationinsights.telemetry.EventTelemetry;
import com.microsoft.applicationinsights.telemetry.ExceptionTelemetry;
import com.microsoft.applicationinsights.telemetry.RemoteDependencyTelemetry;
import com.microsoft.applicationinsights.telemetry.RequestTelemetry;
import org.jetbrains.annotations.NotNull;

public interface ITelemetryClient {

    static ITelemetryClient getInstance() {

        return new ApplicationInsightsTelemetryClient();
    }

    void setRoleInstance(@NotNull String name);

    void setDeviceId(@NotNull String id);

    void setUserId(@NotNull String id);

    void setSessionId(@NotNull String id);

    @NotNull
    String getOperationId();

    void setOperationId(String id);

    void trackException(ExceptionTelemetry telemetry);

    void trackEvent(EventTelemetry telemetry);

    void trackRequest(RequestTelemetry telemetry);

    void trackDependency(RemoteDependencyTelemetry telemetry);

    void sendAllTelemetry();
}
