package jezzsantos.automate.plugin.application.interfaces;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public class PatternDefinition {
    @SerializedName(value = "Id")
    private String id;
    @SerializedName(value = "Name")
    private String name;
    @SerializedName(value = "Version")
    private String version;
    @SerializedName(value = "IsCurrent")
    private boolean isCurrent;

    public PatternDefinition(String id, String name, String version, Boolean isCurrent) {
        this.id = id;
        this.name = name;
        this.version = version;
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

    @Override
    public String toString() {
        return String.format("%s  (v.%s)", this.name, this.version);
    }
}
