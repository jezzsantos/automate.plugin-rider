package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Attribute {

    @SerializedName(value = "Id")
    private String id;
    @SerializedName(value = "Name")
    private String name;
    @SerializedName(value = "DataType")
    private String dataType;
    @SerializedName(value = "IsRequired")
    private boolean isRequired;
    @SerializedName(value = "Choices")
    private List<String> choices = new ArrayList<>();
    @SerializedName(value = "DefaultValue")
    private String defaultValue;

    public Attribute(@NotNull String id, @NotNull String name) {

        this.id = id;
        this.name = name;
    }

    public String getName() {

        return this.name;
    }

    @Override
    public String toString() {

        var choices = this.choices.isEmpty()
          ? ""
          : String.format(", %s: %s",
                          AutomateBundle.message("general.Attribute.Choices.Title"),
                          String.join(";", this.choices));
        var defaultValue = this.defaultValue.isEmpty()
          ? ""
          : String.format(", %s: %s", AutomateBundle.message("general.Attribute.DefaultValue.Title"), this.defaultValue);
        return String.format("%s  (%s, %s%s%s)", this.name, this.dataType, this.isRequired
          ? AutomateBundle.message("general.Attribute.IsRequired.True.Title")
          : AutomateBundle.message("general.Attribute.IsRequired.False.Title"), choices, defaultValue);
    }

    public void setProperties(boolean isRequired, @NotNull String dataType, @Nullable String defaultValue, @Nullable List<String> choices) {

        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
        this.dataType = dataType;
        this.choices = choices == null
          ? this.choices
          : choices;
    }
}
