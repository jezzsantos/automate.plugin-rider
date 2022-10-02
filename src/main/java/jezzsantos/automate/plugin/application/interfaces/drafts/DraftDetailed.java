package jezzsantos.automate.plugin.application.interfaces.drafts;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class DraftDetailed {

    @SerializedName(value = "DraftId")
    private final String id;
    @SerializedName(value = "Name")
    private final String name;
    @SerializedName(value = "ToolkitVersion")
    private final String toolkitVersion;
    @SerializedName(value = "Configuration")
    private final Map<String, Object> configuration;
    private final MustUpgradeInfo mustUpgradeInfo;

    public DraftDetailed(@NotNull String id, @NotNull String name, @NotNull String toolkitVersion, @NotNull HashMap<String, Object> configuration) {

        this.id = id;
        this.name = name;
        this.toolkitVersion = toolkitVersion;
        this.configuration = configuration;
        this.mustUpgradeInfo = null;
    }

    private DraftDetailed(@NotNull String id, @NotNull String name, @NotNull String originalToolkitVersion, @NotNull String currentToolkitVersion) {

        this.id = id;
        this.name = name;
        this.toolkitVersion = currentToolkitVersion;
        this.configuration = Map.of();
        this.mustUpgradeInfo = new MustUpgradeInfo(originalToolkitVersion, currentToolkitVersion);
    }

    public static DraftDetailed createMustUpgrade(@NotNull String id, @NotNull String name, @NotNull String originalToolkitVersion, @NotNull String currentToolkitVersion) {

        return new DraftDetailed(id, name, originalToolkitVersion, currentToolkitVersion);
    }

    @NotNull
    public String getName() {

        return this.name;
    }

    @NotNull
    public String getId() {

        return this.id;
    }

    @NotNull
    public DraftElement getRoot() {

        return new DraftElement(this.name, DraftElement.toElementValueMap(this.configuration), true);
    }

    public boolean mustBeUpgraded() {return this.mustUpgradeInfo != null;}

    @Nullable
    public MustUpgradeInfo getUpgradeInfo() {

        return this.mustUpgradeInfo;
    }

    @Override
    public String toString() {

        return String.format("%s (%s)", this.name, this.id);
    }
}

