package jezzsantos.automate.plugin.application.services.interfaces;

import jezzsantos.automate.core.AutomateConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.module.ModuleDescriptor.Version;
import java.util.Objects;

public class CliExecutableStatus {

    private static final String minSupportedVersion = AutomateConstants.MinimumSupportedVersion;
    private final String executableName;
    private String version;
    private CliVersionCompatibility compatibility;

    public CliExecutableStatus(@NotNull String executableName) {

        this.executableName = executableName;
        initCompatabilityAndVersion(null);
    }

    public CliExecutableStatus(@NotNull String executableName, @NotNull String version) {

        this.executableName = executableName;
        initCompatabilityAndVersion(version);
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

    private void initCompatabilityAndVersion(@Nullable String version) {

        if (version == null) {
            this.compatibility = CliVersionCompatibility.UNKNOWN;
            this.version = "";
            return;
        }

        this.compatibility = calculateCompatibility(version);
        this.version = this.compatibility != CliVersionCompatibility.UNKNOWN
          ? version
          : "";
    }

    private CliVersionCompatibility calculateCompatibility(@NotNull String version) {

        var currentVersion = parseVersion(version);
        if (currentVersion == null) {
            return CliVersionCompatibility.UNKNOWN;
        }
        var minimumVersion = parseVersion(minSupportedVersion);
        if (currentVersion.compareTo(Objects.requireNonNull(minimumVersion)) >= 0) {
            return CliVersionCompatibility.COMPATIBLE;
        }
        return CliVersionCompatibility.INCOMPATIBLE;
    }

    private Version parseVersion(String version) {

        try {
            return Version.parse(version);
        } catch (Exception ignored) {
            return null;
        }
    }
}
