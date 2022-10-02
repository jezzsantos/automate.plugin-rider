package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import jezzsantos.automate.plugin.application.interfaces.drafts.MustUpgradeInfo;
import org.jetbrains.annotations.NotNull;

public class DraftMustBeUpgradedPlaceholderNode {

    @NotNull
    private final String name;
    private final MustUpgradeInfo info;

    public DraftMustBeUpgradedPlaceholderNode(@NotNull String name, @NotNull MustUpgradeInfo info) {

        this.name = name;
        this.info = info;
    }

    @NotNull
    public String getFromVersion() {return this.info.getFromVersion();}

    @NotNull
    public String getToVersion() {return this.info.getToVersion();}

    public boolean isIncompatibleUpgrade() {return this.info.isIncompatibleUpgrade();}

    @Override
    public String toString() {

        return this.name;
    }
}
