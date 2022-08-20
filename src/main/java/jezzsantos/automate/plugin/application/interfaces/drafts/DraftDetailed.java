package jezzsantos.automate.plugin.application.interfaces.drafts;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class DraftDetailed {
    @SerializedName(value = "DraftId")
    private final String id;
    @SerializedName(value = "Name")
    private final String name;
    @SerializedName(value = "Configuration")
    private DraftConfiguration configuration;

    public DraftDetailed(@NotNull String id, @NotNull String name, @NotNull DraftConfiguration configuration) {
        this.id = id;
        this.name = name;
        this.configuration = configuration;
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
        return String.format("%s", this.name);
    }
}
