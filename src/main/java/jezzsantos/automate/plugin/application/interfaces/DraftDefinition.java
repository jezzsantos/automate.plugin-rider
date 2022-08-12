package jezzsantos.automate.plugin.application.interfaces;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public class DraftDefinition {
    @SerializedName(value = "Id")
    private final String id;
    @SerializedName(value = "Name")
    private final String name;

    public DraftDefinition(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.format("%s  (%s)", this.name, this.id.substring(24));
    }
}
