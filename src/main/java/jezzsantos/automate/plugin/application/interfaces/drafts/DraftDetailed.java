package jezzsantos.automate.plugin.application.interfaces.drafts;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class DraftDetailed {

    @SerializedName(value = "DraftId")
    private final String id;
    @SerializedName(value = "Name")
    private final String name;
    @SerializedName(value = "Configuration")
    private final Map<String, Object> configuration;

    public DraftDetailed(@NotNull String id, @NotNull String name, @NotNull HashMap<String, Object> configuration) {

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

        return String.format("%s (%s)", this.name, this.id);
    }

    @NotNull
    public DraftElement getConfiguration() {

        return new DraftElement(this.name, DraftElement.toElementValueMap(this.configuration));
    }

}
