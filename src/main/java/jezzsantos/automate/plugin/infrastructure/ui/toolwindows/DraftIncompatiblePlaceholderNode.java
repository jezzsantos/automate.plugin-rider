package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftVersionCompatibility;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitVersionCompatibility;
import org.jetbrains.annotations.NotNull;

public class DraftIncompatiblePlaceholderNode {

    @NotNull
    private final String draftName;
    @NotNull
    private final String toolkitId;
    @NotNull
    private final DraftVersionCompatibility compatibility;
    @NotNull
    private final String toolkitName;

    public DraftIncompatiblePlaceholderNode(@NotNull String draftName, @NotNull String toolkitId, @NotNull String toolkitName, @NotNull DraftVersionCompatibility compatibility) {

        this.draftName = draftName;
        this.toolkitId = toolkitId;
        this.toolkitName = toolkitName;
        this.compatibility = compatibility;
    }

    public boolean isDraftIncompatible() {return this.compatibility.isDraftIncompatible();}

    public boolean isRuntimeIncompatible() {return this.compatibility.isRuntimeIncompatible();}

    @NotNull
    public String getDraftName() {return this.draftName;}

    @NotNull
    public String getToolkitName() {return this.toolkitName;}

    @NotNull
    public String getToolkitId() {return this.toolkitId;}

    public DraftVersionCompatibility getDraftCompatibility() {return this.compatibility;}

    public ToolkitVersionCompatibility getToolkitCompatibility() {return this.compatibility;}

    @Override
    public String toString() {

        return this.draftName;
    }
}
