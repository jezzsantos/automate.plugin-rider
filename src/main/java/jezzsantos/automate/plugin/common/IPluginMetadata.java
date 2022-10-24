package jezzsantos.automate.plugin.common;

import org.jetbrains.annotations.NotNull;

public interface IPluginMetadata {

    @NotNull
    String getInstallationId();

    @NotNull
    String getRuntimeVersion();

    @NotNull
    String getProductName();
}
