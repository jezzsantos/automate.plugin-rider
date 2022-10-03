package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public class PatternLite {

    @SerializedName(value = "Id")
    private String id;
    @SerializedName(value = "Name")
    private String name;
    @SerializedName(value = "Version")
    private String version;
    @SerializedName(value = "IsCurrent")
    private boolean isCurrent;

    public PatternLite(@NotNull String id, @NotNull String name, @NotNull String version, Boolean isCurrent) {

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

        return String.format("%s  (v%s)", this.name, this.version);
    }
}
