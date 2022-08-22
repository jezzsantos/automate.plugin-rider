package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class CodeTemplate {

    @SerializedName(value = "Id")
    private String id;
    @SerializedName(value = "Name")
    private String name;

    public CodeTemplate(@NotNull String id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
