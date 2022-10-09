package jezzsantos.automate.plugin.application.interfaces.toolkits;

import com.google.gson.annotations.SerializedName;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.core.AutomateConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

public class ToolkitLite {

    @SerializedName(value = "Id")
    private String id;
    @SerializedName(value = "PatternName")
    private String name;
    @SerializedName(value = "Version")
    private ToolkitVersionCompatibility version;

    @UsedImplicitly
    public ToolkitLite() {}

    @TestOnly
    public ToolkitLite(@NotNull String id, @NotNull String name, @NotNull String version) {

        this.id = id;
        this.name = name;
        this.version = new ToolkitVersionCompatibility(version, version, AutomateConstants.ToolkitRuntimeVersionCompatibility.COMPATIBLE);
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
    public ToolkitVersionCompatibility getVersion() {

        return this.version;
    }

    @Override
    public String toString() {

        return String.format("%s  (%s)", this.name, this.id);
    }
}
