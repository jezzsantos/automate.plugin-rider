package jezzsantos.automate.plugin.application.interfaces.drafts;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class DraftConfiguration {
    @SerializedName(value = "Id")
    private final String id;

    public DraftConfiguration(@NotNull String id) {
        this.id = id;
    }
}
