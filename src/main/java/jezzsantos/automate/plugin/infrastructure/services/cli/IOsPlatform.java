package jezzsantos.automate.plugin.infrastructure.services.cli;

import org.jetbrains.annotations.NotNull;

public interface IOsPlatform {

    @NotNull String getCurrentDirectory();

    boolean getIsWindowsOs();

    @NotNull String getDotNetInstallationDirectory();
}
