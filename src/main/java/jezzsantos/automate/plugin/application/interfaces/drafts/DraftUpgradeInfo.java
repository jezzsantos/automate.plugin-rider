package jezzsantos.automate.plugin.application.interfaces.drafts;

import com.intellij.util.text.SemVer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DraftUpgradeInfo {

    private final SemVer originalToolkit;
    private final SemVer currentToolkit;

    private final DraftCompatibility compatibility;

    public DraftUpgradeInfo(@NotNull String originalToolkitVersion, @NotNull String currentToolkitVersion) {

        this.originalToolkit = Objects.requireNonNullElse(SemVer.parseFromText(originalToolkitVersion), SemVer.parseFromText("0.0.0"));
        this.currentToolkit = Objects.requireNonNullElse(SemVer.parseFromText(currentToolkitVersion), SemVer.parseFromText("0.0.0"));

        this.compatibility = calculateCompatibility();
    }

    @NotNull
    public String getFromVersion() {return this.originalToolkit.toString();}

    @NotNull
    public String getToVersion() {return this.currentToolkit.toString();}

    public boolean isCompatible() {return this.compatibility == DraftCompatibility.COMPATIBLE;}

    @SuppressWarnings("unused")
    @NotNull
    public DraftCompatibility getCompatibility() {return this.compatibility;}

    private DraftCompatibility calculateCompatibility() {

        return this.currentToolkit.getMajor() > this.originalToolkit.getMajor()
          ? DraftCompatibility.INCOMPATIBLE_TOOLKIT
          : DraftCompatibility.COMPATIBLE;
    }
}
