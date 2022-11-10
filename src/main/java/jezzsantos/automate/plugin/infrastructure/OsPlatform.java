package jezzsantos.automate.plugin.infrastructure;

import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BiFunction;

public class OsPlatform implements IOsPlatform {

    public static final List<String> DotNetExecutableLocationFallbacksNix = List.of("/usr/local/share/dotnet/", "/usr/local/share/dotnet/x64/");

    @Override
    public boolean getIsWindowsOs() {

        var osName = getOperatingSystemName().toLowerCase();
        return osName.contains("windows");
    }

    @NotNull
    @Override
    public String getDotNetToolsDirectory() {

        return getDotNetToolsDirectory(getIsWindowsOs(), System.getenv(), System.getProperties());
    }

    @NotNull
    @Override
    public String getDotNetInstallationDirectory() {

        return getDotNetInstallationDirectory(getIsWindowsOs(), System.getenv(), (path, filename) -> {
            var dotNetCliFile = new File(path, filename);
            if (dotNetCliFile.isFile()) {
                return Objects.requireNonNull(Try.safely(dotNetCliFile::getCanonicalPath));
            }
            else {
                return null;
            }
        });
    }

    @NotNull
    @Override
    public String getOperatingSystemName() {

        return Objects.requireNonNullElse(System.getProperty("os.name"), "unknown OS");
    }

    @NotNull
    @Override
    public String getOperatingSystemVersion() {

        return Objects.requireNonNullElse(System.getProperty("os.version"), "0.0");
    }

    @TestOnly
    @NotNull
    public String getDotNetInstallationDirectory(boolean isWindows, @NotNull Map<String, String> variables, @NotNull BiFunction<String, String, @Nullable String> getFileIfExists) {

        var dotNetExecutableFilename = isWindows
          ? "dotnet.exe"
          : "dotnet";
        var path = Objects.requireNonNullElse(variables.get("PATH"), "");
        var paths = path.split(String.valueOf(File.pathSeparatorChar));
        for (var pathComponent : paths) {
            var file = getFileIfExists.apply(pathComponent, dotNetExecutableFilename);
            if (file != null) {
                return file;
            }
        }

        if (isWindows) {
            return getWindowsFallbackPath(variables);
        }

        return findNixFallbackPath(getFileIfExists, dotNetExecutableFilename);
    }

    @TestOnly
    @NotNull
    public String getDotNetToolsDirectory(boolean isWindows, @NotNull Map<String, String> variables, @NotNull Properties properties) {

        var windowsPath = Paths.get(variables.get("USERPROFILE"), ".dotnet", "tools") + File.separator;
        var nixPath = Paths.get(properties.getProperty("user.home"), ".dotnet", "tools") + File.separator;

        return isWindows
          ? windowsPath
          : nixPath;
    }

    @NotNull
    private static String findNixFallbackPath(@NotNull BiFunction<String, String, @Nullable String> getFileIfExists, @NotNull String dotNetExecutableFilename) {

        for (var fallbackPath : DotNetExecutableLocationFallbacksNix) {
            var file = getFileIfExists.apply(fallbackPath, dotNetExecutableFilename);
            if (file != null) {
                return file;
            }
        }

        throw new RuntimeException(AutomateBundle.message("exception.OSPlatform.DotNetInstallationDirectory.NotFound"));
    }

    @NotNull
    private static String getWindowsFallbackPath(@NotNull Map<String, String> environmentVariables) {

        return Paths.get(environmentVariables.get("ProgramFiles"), "dotnet") + File.separator;
    }
}
