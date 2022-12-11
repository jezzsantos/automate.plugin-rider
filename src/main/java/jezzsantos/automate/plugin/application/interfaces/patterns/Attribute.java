package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    @UsedImplicitly
    public Attribute() {}

    public Attribute(@NotNull String id, @NotNull String name) {

        this(id, name, false, null, AutomateConstants.AttributeDataType.STRING, new ArrayList<>());
    }

    @TestOnly
    public Attribute(@NotNull String id, @NotNull String name, @NotNull AutomateConstants.AttributeDataType dataType) {

        this(id, name, false, null, dataType, null);
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isValidDataType(@NotNull AutomateConstants.AttributeDataType dataType, String value) {

        switch (dataType) {
            case STRING:
                return true;

            case BOOLEAN:
                if (value == null) {
                    return false;
                }
                return List.of("true", "false").contains(value.toLowerCase());

            case INTEGER:
                if (value == null) {
                    return false;
                }
                return NumberUtils.isDigits(value);

            case FLOAT:
                if (value == null) {
                    return false;
                }
                return NumberUtils.isNumber(value);

            case DATETIME:
                if (value == null) {
                    return false;
                }
                return isIsoDate(value);

            default:
                if (value == null) {
                    return false;
                }
                return false;
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isOneOfChoices(@NotNull List<String> choices, @Nullable String value) {

        if (choices.isEmpty()) {
            return true;
        }

        if (value == null) {
            return false;
        }

        return choices.contains(value);
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

    public boolean isValidDataType(String value) {

        return isValidDataType(this.dataType, value);
    }

    public boolean isOneOfChoices(@Nullable String value) {

        if (value == null) {
            return false;
        }

        return isOneOfChoices(this.choices, value);
    }

    @NotNull
    public String getId() {return this.id;}

    @NotNull
    public String getName() {return this.name;}

    @Nullable
    public String getDefaultValue() {return this.defaultValue;}

    @NotNull
    public AutomateConstants.AttributeDataType getDataType() {return this.dataType;}

    public boolean hasChoices() {return !this.choices.isEmpty();}

    @NotNull
    public List<String> getChoices() {return this.choices;}

    public boolean isRequired() {return this.isRequired;}

    private static boolean isIsoDate(@NotNull String value) {

        try {
            Instant.from(DateTimeFormatter.ISO_INSTANT.parse(value));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
