package jezzsantos.automate.plugin.infrastructure.services.cli;

import org.jetbrains.annotations.NotNull;

public class OsPlatform implements IOsPlatform {

    @Override
    public boolean getIsWindowsOs() {

        return System.getProperty("os.name").startsWith("Windows");
    }

    @Override
    public @NotNull String getDotNetInstallationDirectory() {

        return getIsWindowsOs()
          ? System.getenv("USERPROFILE") + "\\.dotnet\\tools\\"
          : System.getProperty("user.home") + "/.dotnet/tools/";
    }
}
