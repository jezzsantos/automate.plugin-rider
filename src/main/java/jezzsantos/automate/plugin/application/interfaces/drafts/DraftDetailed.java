package jezzsantos.automate.plugin.application.interfaces.drafts;

import com.google.gson.annotations.SerializedName;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.core.AutomateConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class DraftDetailed {

    @Nullable
    private DraftVersionCompatibility compatibility;
    @SerializedName(value = "DraftId")
    private String id;
    @SerializedName(value = "DraftName")
    private String name;
    @SerializedName(value = "ToolkitId")
    private String toolkitId;
    @SerializedName(value = "ToolkitName")
    private String toolkitName;
    @SerializedName(value = "ToolkitVersion")
    private String toolkitVersion;
    @SerializedName(value = "RuntimeVersion")
    private String runtimeVersion;
    @SerializedName(value = "Configuration")
    private Map<String, Object> configuration;

    @UsedImplicitly
    public DraftDetailed() {}

    public DraftDetailed(@NotNull String id, @NotNull String name, @NotNull String toolkitId, @NotNull String toolkitName, @NotNull String toolkitVersion, @NotNull String runtimeVersion, @NotNull HashMap<String, Object> configuration) {

        this.id = id;
        this.name = name;
        this.toolkitId = toolkitId;
        this.toolkitName = toolkitName;
        this.toolkitVersion = toolkitVersion;
        this.runtimeVersion = runtimeVersion;
        this.configuration = configuration;
        this.compatibility = new DraftVersionCompatibility(toolkitVersion, runtimeVersion, AutomateConstants.DraftToolkitVersionCompatibility.COMPATIBLE);
    }

    private DraftDetailed(@NotNull String id, @NotNull String name, @NotNull String toolkitId, @NotNull String toolkitName, @NotNull DraftVersionCompatibility compatibility) {

        this.id = id;
        this.name = name;
        this.toolkitId = toolkitId;
        this.toolkitName = toolkitName;
        this.toolkitVersion = compatibility.getToolkitVersion().getPublished();
        this.runtimeVersion = compatibility.getRuntimeVersion().getPublished();
        this.configuration = Map.of();
        this.compatibility = compatibility;
    }

    public static DraftDetailed createIncompatible(@NotNull String id, @NotNull String name, @NotNull String toolkitId, @NotNull String toolkitName, @NotNull DraftVersionCompatibility compatibility) {

        return new DraftDetailed(id, name, toolkitId, toolkitName, compatibility);
    }

    @NotNull
    public String getName() {return this.name;}

    @NotNull
    public String getId() {return this.id;}

    @NotNull
    public DraftElement getRoot() {

        return new DraftElement(this.name, DraftElement.toElementValueMap(this.configuration), true);
    }

    public boolean isIncompatible() {

        var compatibility = this.compatibility;
        if (compatibility == null) {
            return false;
        }

        return compatibility.isDraftIncompatible() || compatibility.isRuntimeIncompatible();
    }

    @Nullable
    public DraftVersionCompatibility getCompatibility() {

        return this.compatibility;
    }

    @NotNull
    public String getToolkitVersion() {return this.toolkitVersion;}

    @NotNull
    public String getRuntimeVersion() {return this.runtimeVersion;}

    @NotNull
    public String getToolkitId() {return this.toolkitId;}

    @NotNull
    public String getToolkitName() {return this.toolkitName;}

    @Override
    public String toString() {

        return String.format("%s (%s)", this.name, this.id);
    }
}

