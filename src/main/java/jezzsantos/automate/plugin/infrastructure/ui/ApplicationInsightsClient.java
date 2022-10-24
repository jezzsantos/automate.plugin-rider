package jezzsantos.automate.plugin.infrastructure.ui;

import com.microsoft.applicationinsights.TelemetryClient;
import jezzsantos.automate.ApplicationSettings;
import jezzsantos.automate.plugin.common.IContainer;
import jezzsantos.automate.plugin.common.IPluginMetadata;

public class ApplicationInsightsClient {

    private static final String ApplicationInsightsMapNameFormat = "%s JBRD Plugin";
    public static TelemetryClient client;

    public static TelemetryClient getClient() {

        if (client == null) {

            var platform = IContainer.getOsPlatform();
            var pluginMetadata = IContainer.getPluginMetadata();

            client = new TelemetryClient();
            var context = client.getContext();
            context.setInstrumentationKey(ApplicationSettings.setting("applicationInsightsInstrumentationKey"));
            context.getComponent().setVersion(pluginMetadata.getRuntimeVersion());
            context.getCloud().setRole(createCloudRoleName(pluginMetadata));
            context.getDevice().setOperatingSystem(platform.getOperatingSystemName());
            context.getDevice().setOperatingSystemVersion(platform.getOperatingSystemVersion());
        }

        return client;
    }

    private static String createCloudRoleName(IPluginMetadata pluginMetadata) {

        return String.format(ApplicationInsightsMapNameFormat, pluginMetadata.getProductName());
    }
}
