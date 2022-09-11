package jezzsantos.automate.plugin.application.services.interfaces;

import jezzsantos.automate.core.AutomateConstants;
import org.jetbrains.annotations.NotNull;

import java.lang.module.ModuleDescriptor.Version;
import java.util.Objects;

public class CliExecutableStatus {

    private static final String minSupportedVersion = AutomateConstants.MinimumSupportedVersion;
    private final String version;
    private final CliVersionCompatibility compatibility;
    private final String executableName;

    public CliExecutableStatus(@NotNull String executableName) {

        this.executableName = executableName;
        this.compatibility = CliVersionCompatibility.Unknown;
        this.version = "";
    }

    public CliExecutableStatus(@NotNull String executableName, @NotNull String version) {

        this.executableName = executableName;
        this.compatibility = calculateCompatibility(version);
        this.version = this.compatibility != CliVersionCompatibility.Unknown
          ? version
          : "";
    }

    @NotNull
    public String getMinCompatibleVersion() {

        return minSupportedVersion;
    }

    @NotNull
    public String getVersion() {return this.version;}

    @NotNull
    public CliVersionCompatibility getCompatibility() {

        return this.compatibility;
    }

    @NotNull
    public String getExecutableName() {

        return this.executableName;
    }

    private CliVersionCompatibility calculateCompatibility(@NotNull String version) {

        var currentVersion = parseVersion(version);
        if (currentVersion == null) {
            return CliVersionCompatibility.Unknown;
        }
        var minimumVersion = parseVersion(minSupportedVersion);
        if (currentVersion.compareTo(Objects.requireNonNull(minimumVersion)) >= 0) {
            return CliVersionCompatibility.Supported;
        }
        return CliVersionCompatibility.UnSupported;
    }

    private Version parseVersion(String version) {

        try {
            return Version.parse(version);
        } catch (Exception ignored) {
            return null;
        }
    }
}
