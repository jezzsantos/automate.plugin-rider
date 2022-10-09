package jezzsantos.automate.plugin.application.interfaces.drafts;

import com.google.gson.annotations.SerializedName;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitVersionCompatibility;
import org.jetbrains.annotations.NotNull;

public class DraftVersionCompatibility extends ToolkitVersionCompatibility {

    @SerializedName(value = "DraftCompatibility")
    private AutomateConstants.DraftToolkitVersionCompatibility draftCompatibility;

    @UsedImplicitly
    public DraftVersionCompatibility() {}

    public DraftVersionCompatibility(@NotNull String toolkitVersion, @NotNull String runtimeVersion, @NotNull AutomateConstants.DraftToolkitVersionCompatibility compatibility) {

        super(toolkitVersion, runtimeVersion, AutomateConstants.ToolkitRuntimeVersionCompatibility.COMPATIBLE);
        this.draftCompatibility = compatibility;
    }

    public DraftVersionCompatibility(@NotNull String toolkitVersion, @NotNull String runtimeVersion, @NotNull AutomateConstants.ToolkitRuntimeVersionCompatibility compatibility) {

        super(toolkitVersion, runtimeVersion, compatibility);
        this.draftCompatibility = AutomateConstants.DraftToolkitVersionCompatibility.COMPATIBLE;
    }

    public boolean isDraftIncompatible() {return this.draftCompatibility != AutomateConstants.DraftToolkitVersionCompatibility.COMPATIBLE;}

    public AutomateConstants.DraftToolkitVersionCompatibility getDraftCompatibility() {return this.draftCompatibility;}
}
