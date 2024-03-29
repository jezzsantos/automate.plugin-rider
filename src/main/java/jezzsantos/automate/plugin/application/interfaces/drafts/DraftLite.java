package jezzsantos.automate.plugin.application.interfaces.drafts;

import com.google.gson.annotations.SerializedName;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.core.AutomateConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

@SuppressWarnings("unused")
public class DraftLite {

    @SerializedName(value = "DraftId")
    private String id;
    @SerializedName(value = "DraftName")
    private String name;
    @SerializedName(value = "ToolkitId")
    private String toolkitId;
    @SerializedName(value = "ToolkitName")
    private String toolkitName;
    @SerializedName(value = "ToolkitVersion")
    private DraftVersionCompatibility toolkitVersion;
    @SerializedName(value = "IsCurrent")
    private boolean isCurrent;

    @UsedImplicitly
    public DraftLite() {}

    public DraftLite(@NotNull String id, @NotNull String name, @NotNull String toolkitId, @NotNull String toolkitName, @NotNull String toolkitVersion, @NotNull String runtimeVersion, Boolean isCurrent) {

        this(id, name, toolkitId, toolkitName, new DraftVersionCompatibility(toolkitVersion, runtimeVersion, AutomateConstants.DraftToolkitVersionCompatibility.COMPATIBLE),
             isCurrent);
    }

    @TestOnly
    public DraftLite(@NotNull String id, @NotNull String name, @NotNull String toolkitId, @NotNull String toolkitName, @NotNull String toolkitVersion, @NotNull String runtimeVersion, @NotNull AutomateConstants.DraftToolkitVersionCompatibility compatibility, Boolean isCurrent) {

        this(id, name, toolkitId, toolkitName, new DraftVersionCompatibility(toolkitVersion, runtimeVersion, compatibility), isCurrent);
    }

    @TestOnly
    public DraftLite(@NotNull String id, @NotNull String name, @NotNull String toolkitId, @NotNull String toolkitName, @NotNull String toolkitVersion, @NotNull String runtimeVersion, @NotNull AutomateConstants.ToolkitRuntimeVersionCompatibility compatibility, Boolean isCurrent) {

        this(id, name, toolkitId, toolkitName, new DraftVersionCompatibility(toolkitVersion, runtimeVersion, compatibility), isCurrent);
    }

    private DraftLite(@NotNull String id, @NotNull String name, @NotNull String toolkitId, @NotNull String toolkitName, @NotNull DraftVersionCompatibility version, Boolean isCurrent) {

        this.id = id;
        this.name = name;
        this.toolkitVersion = version;
        this.toolkitId = toolkitId;
        this.toolkitName = toolkitName;
        this.isCurrent = isCurrent;
    }

    @NotNull
    public String getName() {return this.name;}

    @NotNull
    public String getId() {return this.id;}

    public boolean getIsCurrent() {return this.isCurrent;}

    public boolean isIncompatible() {

        return this.toolkitVersion.isDraftIncompatible() || this.toolkitVersion.isRuntimeIncompatible();
    }

    public DraftVersionCompatibility getVersion() {return this.toolkitVersion;}

    public String getToolkitId() {return this.toolkitId;}

    public String getToolkitName() {return this.toolkitName;}

    @Override
    public String toString() {

        return String.format("%s  (v%s)", this.name, this.toolkitVersion.getToolkitVersion().getInstalled());
    }
}
