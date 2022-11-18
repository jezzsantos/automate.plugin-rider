package jezzsantos.automate.plugin.infrastructure;

import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.common.recording.LogLevel;
import jezzsantos.automate.plugin.common.recording.Recorder;
import jezzsantos.automate.plugin.infrastructure.reporting.ApplicationInsightsTelemetryClient;
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
    public static final String PathVariableOnWindows = "Path";
    public static final String PathVariableOnNix = "PATH";
    private static String cachedInstalledPath;
    private static String cachedToolsPath;
    private static Boolean cachedIsWindows;
    private final IRecorder recorder;

    public OsPlatform(@NotNull IRecorder recorder) {

        this.recorder = recorder;
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
    public String getOperatingSystemName() {

        return InternalAccess.getOperatingSystemName();
    }

    @NotNull
    @Override
    public String getOperatingSystemVersion() {

        return InternalAccess.getOperatingSystemVersion();
    }

    @NotNull
    @Override
    public String getDotNetToolsDirectory() {

        if (cachedToolsPath == null) {
            cachedToolsPath = getDotNetToolsDirectory(getIsWindowsOs(), System.getenv(), System.getProperties());
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
        }

        return cachedInstalledPath;
    }

    @TestOnly
    @NotNull
    public String getDotNetInstallationDirectory(boolean isWindows, @NotNull Map<String, String> variables, @NotNull BiFunction<String, String, @Nullable String> getFileIfExists) {

        var osName = getOperatingSystemName();
        var dotNetExecutableFilename = isWindows
          ? "dotnet.exe"
          : "dotnet";
        var pathVariableName = isWindows
          ? PathVariableOnWindows
          : PathVariableOnNix;
        var path = Objects.requireNonNullElse(variables.get(pathVariableName), "");
        var paths = path.split(String.valueOf(File.pathSeparatorChar));
        for (var pathComponent : paths) {
            var file = getFileIfExists.apply(pathComponent, dotNetExecutableFilename);
            if (file != null) {
                this.recorder.trace(LogLevel.INFORMATION, AutomateBundle.message("trace.OsPlatform.InstallationDirectory.PathVariable.Found.Message", pathComponent));
                this.recorder.measureEvent("osplatform.installationdirectory.pathvariable", Map.of(
                  "source", pathVariableName,
                  "location", pathComponent,
                  "os", osName
                ));
                return pathComponent;
            }
        }

        if (paths.length == 0) {
            this.recorder.trace(LogLevel.INFORMATION, AutomateBundle.message("trace.OsPlatform.InstallationDirectory.PathVariable.None.Message"));
        }
        else {
            this.recorder.trace(LogLevel.INFORMATION, AutomateBundle.message("trace.OsPlatform.InstallationDirectory.PathVariable.Missing.Message"));
        }

        String location;
        if (isWindows) {
            location = getWindowsFallbackPath(variables);
        }
        else {
            location = findNixFallbackPath(getFileIfExists, dotNetExecutableFilename);
        }

        if (location == null) {
            throw new RuntimeException(AutomateBundle.message("exception.OSPlatform.DotNetInstallationDirectory.NotFound"));
        }

        this.recorder.trace(LogLevel.INFORMATION, AutomateBundle.message("trace.OsPlatform.InstallationDirectory.FallBack.Message", location));
        this.recorder.measureEvent("osplatform.installationdirectory.fallback", Map.of(
          "source", "fallbacks",
          "location", location,
          "os", osName
        ));

        return location;
    }

    @TestOnly
    @NotNull
    public String getDotNetToolsDirectory(boolean isWindows, @NotNull Map<String, String> variables, @NotNull Properties properties) {

        var osName = getOperatingSystemName();
        var homePath = isWindows
          ? variables.get("USERPROFILE")
          : properties.getProperty("user.home");
        var location = Paths.get(homePath, ".dotnet", "tools") + File.separator;

        this.recorder.trace(LogLevel.INFORMATION, AutomateBundle.message("trace.OsPlatform.ToolsDirectory.Found.Message", location));
        this.recorder.measureEvent("osplatform.toolsdirectory", Map.of(
          "location", location,
          "os", osName
        ));

        return location;
    }

    @Nullable
    private static String findNixFallbackPath(@NotNull BiFunction<String, String, @Nullable String> getFileIfExists, @NotNull String dotNetExecutableFilename) {

        for (var fallbackPath : DotNetExecutableLocationFallbacksNix) {
            var file = getFileIfExists.apply(fallbackPath, dotNetExecutableFilename);
            if (file != null) {
                return fallbackPath;
            }
        }

        return null;
    }

    @Nullable
    private static String getWindowsFallbackPath(@NotNull Map<String, String> environmentVariables) {

        var path = environmentVariables.get("ProgramFiles");
        if (path == null || path.isEmpty()) {
            return null;
        }

        return Paths.get(path, "dotnet") + File.separator;
    }

    /**
     * This class exists to provide an internal version of these methods to be called from dependencies of a {@link IRecorder}.
     * It works around the cyclic dependency between {@link OsPlatform} to {@link ApplicationInsightsTelemetryClient} through {@link Recorder}
     */
    public static class InternalAccess {

        @NotNull
        public static String getOperatingSystemName() {

            return Objects.requireNonNullElse(System.getProperty("os.name"), "unknown OS");
        }

        @NotNull
        public static String getOperatingSystemVersion() {

            return Objects.requireNonNullElse(System.getProperty("os.version"), "0.0");
        }
    }
}
