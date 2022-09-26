package jezzsantos.automate.plugin.infrastructure.services.cli;

import org.jetbrains.annotations.NotNull;

public interface IOsPlatform {

    boolean getIsWindowsOs();

    @NotNull String getDotNetInstallationDirectory();
}
