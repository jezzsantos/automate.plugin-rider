package jezzsantos.automate.plugin.application.interfaces.drafts;

import com.intellij.util.text.SemVer;
import org.jetbrains.annotations.NotNull;

public class MustUpgradeInfo {

    private final String originalToolkitVersion;
    private final String currentToolkitVersion;
    private final boolean isIncompatibleUpgrade;

    public MustUpgradeInfo(@NotNull String originalToolkitVersion, @NotNull String currentToolkitVersion) {

        this.originalToolkitVersion = originalToolkitVersion;
        this.currentToolkitVersion = currentToolkitVersion;

        var fromVersion = SemVer.parseFromText(originalToolkitVersion);
        var toVersion = SemVer.parseFromText(currentToolkitVersion);

        assert toVersion != null;
        assert fromVersion != null;
        this.isIncompatibleUpgrade = toVersion.getMajor() > fromVersion.getMajor();
    }

    public String getFromVersion() {return this.originalToolkitVersion;}

    public String getToVersion() {return this.currentToolkitVersion;}

    public boolean isIncompatibleUpgrade() {return this.isIncompatibleUpgrade;}
}
