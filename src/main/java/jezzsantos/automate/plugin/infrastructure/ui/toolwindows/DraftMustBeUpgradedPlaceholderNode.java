package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftUpgradeInfo;
import org.jetbrains.annotations.NotNull;

public class DraftMustBeUpgradedPlaceholderNode {

    @NotNull
    private final String name;
    private final DraftUpgradeInfo info;

    public DraftMustBeUpgradedPlaceholderNode(@NotNull String name, @NotNull DraftUpgradeInfo info) {

        this.name = name;
        this.info = info;
    }

    @NotNull
    public String getFromVersion() {return this.info.getFromVersion();}

    @NotNull
    public String getToVersion() {return this.info.getToVersion();}

    public boolean mustUpgrade() {return !this.info.isCompatible();}

    @Override
    public String toString() {

        return this.name;
    }
}
