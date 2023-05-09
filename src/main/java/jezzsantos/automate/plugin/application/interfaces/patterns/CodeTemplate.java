package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @SerializedName(value = "EditorPath")
    private String editorPath;

    @UsedImplicitly
    public CodeTemplate() {}

    public CodeTemplate(@NotNull String id, @NotNull String name) {

        this(id, name, null, null, null);
    }

    public CodeTemplate(@NotNull String id, @NotNull String name, @Nullable String originalFilePath, @Nullable String originalFileExtension, @Nullable String editorPath) {

        this.id = id;
        this.name = name;
        this.originalFilePath = originalFilePath;
        this.originalFileExtension = originalFileExtension;
        this.editorPath = editorPath;
    }

    @Override
    public String toString() {

        var filePath = String.format("%s: %s",
                                     AutomateBundle.message("general.Automation.CodeTemplate.FilePath.Title"),
                                     this.originalFilePath);
        return String.format("%s (%s)", this.name, filePath);
    }

    @NotNull
    public String getName() {

        return this.name;
    }

    @Nullable
    public String getEditorPath() {

        return this.editorPath;
    }
}
