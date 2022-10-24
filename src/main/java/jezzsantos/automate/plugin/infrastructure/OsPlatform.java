package jezzsantos.automate.plugin.infrastructure;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OsPlatform implements IOsPlatform {

    @Override
    public boolean getIsWindowsOs() {

        return getOperatingSystemName().startsWith("Windows");
    }

    @Override
    public @NotNull String getDotNetInstallationDirectory() {

        return getIsWindowsOs()
          ? System.getenv("USERPROFILE") + "\\.dotnet\\tools\\"
          : System.getProperty("user.home") + "/.dotnet/tools/";
    }

    @Override
    public @NotNull String getOperatingSystemName() {

        return Objects.requireNonNullElse(System.getProperty("os.name"), "unknownos");
    }

    @Override
    public @NotNull String getOperatingSystemVersion() {

        return Objects.requireNonNullElse(System.getProperty("os.version"), "0.0");
    }
}
