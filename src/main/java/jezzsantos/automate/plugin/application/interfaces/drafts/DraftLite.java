package jezzsantos.automate.plugin.application.interfaces.drafts;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

@SuppressWarnings("unused")
public class DraftLite {

    @SerializedName(value = "Id")
    private final String id;
    @SerializedName(value = "Name")
    private final String name;
    @SerializedName(value = "ToolkitId")
    private String toolkitId;
    @SerializedName(value = "ToolkitVersion")
    private String originalToolkitVersion;
    @SerializedName(value = "CurrentToolkitVersion")
    private String currentToolkitVersion;
    @SerializedName(value = "IsCurrent")
    private boolean isCurrent;

    public DraftLite(@NotNull String id, @NotNull String name, @NotNull String toolkitId, @NotNull String originalToolkitVersion, Boolean isCurrent) {

        this(id, name, toolkitId, originalToolkitVersion, originalToolkitVersion, isCurrent);
    }

    @TestOnly
    public DraftLite(@NotNull String id, @NotNull String name, @NotNull String toolkitId, @NotNull String originalToolkitVersion, @NotNull String currentToolkitVersion, Boolean isCurrent) {

        this.id = id;
        this.name = name;
        this.originalToolkitVersion = originalToolkitVersion;
        this.currentToolkitVersion = currentToolkitVersion;
        this.toolkitId = toolkitId;
        this.isCurrent = isCurrent;
    }

    @NotNull
    public String getName() {

        return this.name;
    }

    @NotNull
    public String getId() {

        return this.id;
    }

    public boolean getIsCurrent() {

        return this.isCurrent;
    }

    public boolean isOutOfDate() {

        return !this.originalToolkitVersion.equalsIgnoreCase(this.currentToolkitVersion);
    }

    public String getCurrentToolkitVersion() {

        return this.currentToolkitVersion;
    }

    public String getOriginalToolkitVersion() {

        return this.originalToolkitVersion;
    }

    @Override
    public String toString() {

        return String.format("%s  (v.%s)", this.name, this.originalToolkitVersion);
    }
}
