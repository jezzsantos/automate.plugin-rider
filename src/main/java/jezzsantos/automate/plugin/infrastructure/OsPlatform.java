package jezzsantos.automate.plugin.infrastructure;

import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.common.recording.LogLevel;
import jezzsantos.automate.plugin.common.recording.LoggingOnlyRecorder;
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

    public static final List<String> DotNetExecutableLocationFallbacksNix = List.of(
      "/usr/local/share/dotnet/",
      "/usr/local/share/dotnet/x64/",
      "/usr/bin",
      "/usr/share/dotnet");
    private static String cachedInstalledPath;
    private static String cachedToolsPath;
    private static Boolean cachedIsWindows;
    private final IRecorder recorder;

    public OsPlatform() {

        this.recorder = new LoggingOnlyRecorder();
    }

    @Override
    public boolean getIsWindowsOs() {

        if (cachedIsWindows == null) {
            var osName = getOperatingSystemName().toLowerCase();
            cachedIsWindows = osName.contains("windows");
        }

        return cachedIsWindows;
    }

    @NotNull
    @Override
    public String getDotNetToolsDirectory() {

        if (cachedToolsPath == null) {
            cachedToolsPath = getDotNetToolsDirectory(getIsWindowsOs(), System.getenv(), System.getProperties());

            this.recorder.trace(LogLevel.INFORMATION,
                                AutomateBundle.message("trace.OsPlatform.DotNetToolsPath.Message", cachedToolsPath));
        }

        return cachedToolsPath;
    }

    @NotNull
    @Override
    public String getDotNetInstallationDirectory() {

        if (cachedInstalledPath == null) {

            cachedInstalledPath = getDotNetInstallationDirectory(getIsWindowsOs(), System.getenv(), (path, filename) -> {
                var dotNetCliFile = new File(path, filename);
                if (dotNetCliFile.isFile()) {
                    return Objects.requireNonNull(Try.safely(dotNetCliFile::getCanonicalPath));
                }
                else {
                    return null;
                }
            });

            this.recorder.trace(LogLevel.INFORMATION,
                                AutomateBundle.message("trace.OsPlatform.DotNetInstallationPath.Message", cachedInstalledPath));
        }

        return cachedInstalledPath;
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
        var pathVariableName = isWindows
          ? "Path"
          : "PATH";
        var path = Objects.requireNonNullElse(variables.get(pathVariableName), "");
        var paths = path.split(String.valueOf(File.pathSeparatorChar));
        for (var pathComponent : paths) {
            var file = getFileIfExists.apply(pathComponent, dotNetExecutableFilename);
            if (file != null) {
                this.recorder.trace(LogLevel.INFORMATION, "dotnet executable was found in PATH environment variable, installed at '%s'", pathComponent);
                return pathComponent;
            }
        }

        if (paths.length == 0) {
            this.recorder.trace(LogLevel.INFORMATION, "dotnet executable could not be found in an empty PATH environment variable, searching in fallback paths");
        }
        else {
            this.recorder.trace(LogLevel.INFORMATION, "dotnet executable could not be found in any of the paths of the PATH environment variable, searching in fallback paths");
        }

        if (isWindows) {
            var location = getWindowsFallbackPath(variables);
            this.recorder.trace(LogLevel.INFORMATION, "dotnet executable was found installed at fallback path '%s'", location);
            return location;
        }

        var location = findNixFallbackPath(getFileIfExists, dotNetExecutableFilename);
        this.recorder.trace(LogLevel.INFORMATION, "dotnet executable was found installed at fallback path '%s'", location);
        return location;
    }

    @TestOnly
    @NotNull
    public String getDotNetToolsDirectory(boolean isWindows, @NotNull Map<String, String> variables, @NotNull Properties properties) {

        var location = isWindows
          ? Paths.get(variables.get("USERPROFILE"), ".dotnet", "tools") + File.separator
          : Paths.get(properties.getProperty("user.home"), ".dotnet", "tools") + File.separator;

        this.recorder.trace(LogLevel.INFORMATION, "dotnet tools directory is '%s'", location);
        return location;
    }

    @NotNull
    private static String findNixFallbackPath(@NotNull BiFunction<String, String, @Nullable String> getFileIfExists, @NotNull String dotNetExecutableFilename) {

        for (var fallbackPath : DotNetExecutableLocationFallbacksNix) {
            var file = getFileIfExists.apply(fallbackPath, dotNetExecutableFilename);
            if (file != null) {
                return fallbackPath;
            }
        }

        throw new RuntimeException(AutomateBundle.message("exception.OSPlatform.DotNetInstallationDirectory.NotFound"));
    }

    @NotNull
    private static String getWindowsFallbackPath(@NotNull Map<String, String> environmentVariables) {

        return Paths.get(environmentVariables.get("ProgramFiles"), "dotnet") + File.separator;
    }
}
