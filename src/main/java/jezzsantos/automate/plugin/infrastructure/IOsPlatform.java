package jezzsantos.automate.plugin.infrastructure;

import org.jetbrains.annotations.NotNull;

public interface IOsPlatform {

    boolean getIsWindowsOs();

    @NotNull
    String getDotNetInstallationDirectory();

    @NotNull
    String getOperatingSystemName();

    @NotNull
    String getOperatingSystemVersion();
}
