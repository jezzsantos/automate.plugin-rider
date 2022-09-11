package jezzsantos.automate.plugin.application.interfaces.toolkits;

import com.google.gson.annotations.SerializedName;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import org.jetbrains.annotations.NotNull;

public class ToolkitDetailed {

    @SerializedName(value = "ToolkitId")
    private String id;
    @SerializedName(value = "Name")
    private String name;
    @SerializedName(value = "Version")
    private String version;
    @SerializedName(value = "Schema")
    private PatternElement pattern;

    public ToolkitDetailed(@NotNull String id, @NotNull String name, @NotNull String version, @NotNull PatternElement pattern) {

        this.id = id;
        this.name = name;
        this.version = version;
        this.pattern = pattern;
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

    @NotNull
    public PatternElement getPattern() {

        this.pattern.setRoot();
        return this.pattern;
    }

    @Override
    public String toString() {

        return String.format("%s  (%s)", this.name, this.id);
    }
}
