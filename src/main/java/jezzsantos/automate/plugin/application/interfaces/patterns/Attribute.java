package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class Attribute {

    @SerializedName(value = "Id")
    private String id;
    @SerializedName(value = "Name")
    private String name;
    @SerializedName(value = "DataType")
    private AutomateConstants.AttributeDataType dataType;
    @SerializedName(value = "IsRequired")
    private boolean isRequired;
    @SerializedName(value = "Choices")
    private List<String> choices;
    @SerializedName(value = "DefaultValue")
    private String defaultValue;

    public Attribute(@NotNull String id, @NotNull String name) {

        this(id, name, false, null, AutomateConstants.AttributeDataType.STRING, new ArrayList<>());
    }

    @TestOnly
    public Attribute(@NotNull String id, @NotNull String name, boolean isRequired, @Nullable String defaultValue, @Nullable AutomateConstants.AttributeDataType dataType, @Nullable List<String> choices) {

        this.id = id;
        this.name = name;
        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
        this.dataType = dataType != null
          ? dataType
          : AutomateConstants.AttributeDataType.STRING;
        this.choices = choices != null
          ? choices
          : new ArrayList<>();
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
        var defaultValue = this.defaultValue == null || this.defaultValue.isEmpty()
          ? ""
          : String.format(", %s: %s", AutomateBundle.message("general.Attribute.DefaultValue.Title"), this.defaultValue);
        return String.format("%s  (%s, %s%s%s)", this.name, this.dataType, this.isRequired
          ? AutomateBundle.message("general.Attribute.IsRequired.True.Title")
          : AutomateBundle.message("general.Attribute.IsRequired.False.Title"), choices, defaultValue);
    }

    public void setProperties(boolean isRequired, @NotNull String dataType, @Nullable String defaultValue, @Nullable List<String> choices) {

        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
        this.dataType = Enum.valueOf(AutomateConstants.AttributeDataType.class, dataType);
        this.choices = choices == null
          ? this.choices
          : choices;
    }
}
