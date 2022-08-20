package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class PatternDetailed {
    @SerializedName(value = "PatternId")
    private String id;
    @SerializedName(value = "Name")
    private String name;
    @SerializedName(value = "Version")
    private String version;
    @SerializedName(value = "Tree")
    private PatternTree tree;

    public PatternDetailed(@NotNull String id, @NotNull String name, @NotNull String version, @NotNull PatternTree tree) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.tree = tree;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return String.format("%s  (v.%s)", this.name, this.version);
    }
}
