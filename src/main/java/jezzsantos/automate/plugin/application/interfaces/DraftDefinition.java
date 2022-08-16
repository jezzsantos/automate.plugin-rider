package jezzsantos.automate.plugin.application.interfaces;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public class DraftDefinition {
    @SerializedName(value = "Id")
    private final String id;
    @SerializedName(value = "Name")
    private final String name;
    @SerializedName(value = "ToolkitId")
    private String toolkitId;
    @SerializedName(value = "Version")
    private String version;
    @SerializedName(value = "IsCurrent")
    private boolean isCurrent;

    public DraftDefinition(@NotNull String id, @NotNull String name, @NotNull String toolkitId, @NotNull String version, Boolean isCurrent) {
        this.id = id;
        this.name = name;
        this.version = version;
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

    @NotNull
    public String getToolkitId() {
        return this.toolkitId;
    }

    public boolean getIsCurrent() {
        return this.isCurrent;
    }

    @Override
    public String toString() {
        return String.format("%s  (v.%s)", this.name, this.version);
    }
}
