package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class CodeTemplate {

    @SerializedName(value = "Id")
    private String id;
    @SerializedName(value = "Name")
    private String name;
    @SerializedName(value = "OriginalFilePath")
    private String originalFilePath;
    @SerializedName(value = "OriginalFileExtension")
    private String originalFileExtension;

    public CodeTemplate(@NotNull String id, @NotNull String name) {

        this.id = id;
        this.name = name;
    }

    public String getName() {

        return this.name;
    }

    @Override
    public String toString() {

        var filePath = String.format("original: %s", this.originalFilePath);
        return String.format("%s (%s)", this.name, filePath);
    }
}
