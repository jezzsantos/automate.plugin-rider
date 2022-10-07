package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import com.jetbrains.rd.util.UsedImplicitly;
import org.jetbrains.annotations.NotNull;

public class PatternLite {

    @SerializedName(value = "Id")
    private String id;
    @SerializedName(value = "Name")
    private String name;
    @SerializedName(value = "Version")
    private PatternVersion version;
    @SerializedName(value = "IsCurrent")
    private boolean isCurrent;

    @UsedImplicitly
    public PatternLite() {}

    public PatternLite(@NotNull String id, @NotNull String name, @NotNull PatternVersion version, Boolean isCurrent) {

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

    public PatternVersion getVersion() {

        return this.version;
    }

    @Override
    public String toString() {

        return String.format("%s  (v%s)", this.name, this.version);
    }
}
