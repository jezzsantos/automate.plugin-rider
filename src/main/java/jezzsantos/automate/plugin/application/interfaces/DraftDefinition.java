package jezzsantos.automate.plugin.application.interfaces;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public class DraftDefinition {
    @SerializedName(value = "Id")
    private final String id;
    @SerializedName(value = "Name")
    private final String name;
    @SerializedName(value = "Version")
    private String version;
    @SerializedName(value = "IsCurrent")
    private boolean isCurrent;

    public DraftDefinition(String id, String name, String version, Boolean isCurrent) {
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
