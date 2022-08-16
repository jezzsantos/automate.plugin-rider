package jezzsantos.automate.plugin.application.interfaces;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public class ToolkitDefinition {
    @SerializedName(value = "Id")
    private String id;
    @SerializedName(value = "ToolkitName")
    private String name;
    @SerializedName(value = "Version")
    private String version;

    public ToolkitDefinition(@NotNull String id, @NotNull String name, @NotNull String version) {
        this.id = id;
        this.name = name;
        this.version = version;
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
    public String getVersion() {
        return this.version;
    }

    @Override
    public String toString() {
        return String.format("%s  (%s)", this.name, this.id);
    }
}
