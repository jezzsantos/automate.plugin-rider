package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftVersionCompatibility;
import org.jetbrains.annotations.NotNull;

public class DraftMustBeUpgradedPlaceholderNode {

    @NotNull
    private final String name;
    private final DraftVersionCompatibility compatibility;

    public DraftMustBeUpgradedPlaceholderNode(@NotNull String name, @NotNull DraftVersionCompatibility compatibility) {

        this.name = name;
        this.compatibility = compatibility;
    }

    @NotNull
    public String getFromVersion() {return this.compatibility.getToolkitVersion().getCreated();}

    @NotNull
    public String getToVersion() {return this.compatibility.getToolkitVersion().getInstalled();}

    public boolean isDraftIncompatible() {return this.compatibility.isDraftIncompatible();}

    public boolean isToolkitIncompatible() {return this.compatibility.isToolkitIncompatible();}

    @Override
    public String toString() {

        return this.name;
    }
}
