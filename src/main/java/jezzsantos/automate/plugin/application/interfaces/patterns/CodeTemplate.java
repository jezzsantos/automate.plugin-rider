package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

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

        this(id, name, null, null);
    }

    @TestOnly
    public CodeTemplate(@NotNull String id, @NotNull String name, @Nullable String originalFilePath, @Nullable String originalFileExtension) {

        this.id = id;
        this.name = name;
        this.originalFilePath = originalFilePath;
        this.originalFileExtension = originalFileExtension;
    }

    public String getName() {

        return this.name;
    }

    @Override
    public String toString() {

        var filePath = String.format("%s: %s",
                                     AutomateBundle.message("general.Automation.CodeTemplate.FilePath.Title"),
                                     this.originalFilePath);
        return String.format("%s (%s)", this.name, filePath);
    }
}
