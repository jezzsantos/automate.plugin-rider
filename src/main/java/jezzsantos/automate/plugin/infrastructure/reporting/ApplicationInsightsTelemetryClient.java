package jezzsantos.automate.plugin.infrastructure.reporting;

import com.intellij.openapi.Disposable;
import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.telemetry.EventTelemetry;
import com.microsoft.applicationinsights.telemetry.ExceptionTelemetry;
import com.microsoft.applicationinsights.telemetry.RemoteDependencyTelemetry;
import com.microsoft.applicationinsights.telemetry.RequestTelemetry;
import jezzsantos.automate.ApplicationSettings;
import jezzsantos.automate.plugin.common.IContainer;
import jezzsantos.automate.plugin.common.IPluginMetadata;
import org.jetbrains.annotations.NotNull;

public class ApplicationInsightsTelemetryClient implements ITelemetryClient, Disposable {

    private static final String ApplicationInsightsMapNameFormat = "%s JBRD Plugin";
    public TelemetryClient client;

    public ApplicationInsightsTelemetryClient() {

        this.client = createClient();
    }

    @Override
    public void dispose() {

        if (this.client != null) {
            this.client.flush();
        }
    }

    @Override
    public void setRoleInstance(@NotNull String name) {

        this.client.getContext().getCloud().setRoleInstance(name);
    }

    @Override
    public void setDeviceId(@NotNull String id) {

        this.client.getContext().getDevice().setId(id);
    }

    @Override
    public void setUserId(@NotNull String id) {

        this.client.getContext().getUser().setId(id);
    }

    @Override
    public void setSessionId(@NotNull String id) {

        this.client.getContext().getSession().setId(id);
    }

    @NotNull
    @Override
    public String getOperationId() {

        return this.client.getContext().getOperation().getId();
    }

    @Override
    public void setOperationId(String id) {

        this.client.getContext().getOperation().setId(id);
    }

    @Override
    public void trackException(ExceptionTelemetry telemetry) {

        this.client.trackException(telemetry);
    }

    @Override
    public void trackEvent(EventTelemetry telemetry) {

        this.client.trackEvent(telemetry);
    }

    @Override
    public void trackRequest(RequestTelemetry telemetry) {

        this.client.trackRequest(telemetry);
    }

    @Override
    public void trackDependency(RemoteDependencyTelemetry telemetry) {

        this.client.trackDependency(telemetry);
    }

    @Override
    public void sendAllTelemetry() {

        this.client.flush();
    }

    private static String createCloudRoleName(IPluginMetadata pluginMetadata) {

        return String.format(ApplicationInsightsMapNameFormat, pluginMetadata.getProductName());
    }

    private TelemetryClient createClient() {

        var platform = IContainer.getOsPlatform();
        var pluginMetadata = IContainer.getPluginMetadata();

        this.client = new TelemetryClient();
        var context = this.client.getContext();
        context.setInstrumentationKey(ApplicationSettings.setting("applicationInsightsInstrumentationKey"));
        context.getComponent().setVersion(pluginMetadata.getRuntimeVersion());
        context.getCloud().setRole(createCloudRoleName(pluginMetadata));
        context.getDevice().setOperatingSystem(platform.getOperatingSystemName());
        context.getDevice().setOperatingSystemVersion(platform.getOperatingSystemVersion());

        return this.client;
    }
}

