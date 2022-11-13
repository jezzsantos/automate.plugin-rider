package jezzsantos.automate.plugin.infrastructure;

import org.jetbrains.annotations.NotNull;

public interface IOsPlatform {

    boolean getIsWindowsOs();

    @NotNull
    String getOperatingSystemName();

    @SuppressWarnings("unused")
    @NotNull
    String getOperatingSystemVersion();

    @NotNull
    String getDotNetToolsDirectory();

    @NotNull
    String getDotNetInstallationDirectory();
}
