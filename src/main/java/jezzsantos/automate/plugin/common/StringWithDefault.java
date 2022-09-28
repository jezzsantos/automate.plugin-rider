package jezzsantos.automate.plugin.common;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class StringWithDefault {

    private static final String emptyValue = "";
    private final String defaultValue;
    private String currentValue;

    public StringWithDefault(@NotNull String defaultValue) {

        this.defaultValue = defaultValue;
        this.currentValue = emptyValue;
    }

    public StringWithDefault(@NotNull String defaultValue, @NotNull String currentValue) {

        this.defaultValue = defaultValue;
        this.currentValue = isDefaultOrEmpty(currentValue)
          ? emptyValue
          : currentValue;
    }

    public static StringWithDefault fromValue(@NotNull String value) {

        return new StringWithDefault(value);
    }

    @NotNull
    public String getValue() {return this.currentValue;}

    public void setValue(@NotNull String value) {

        this.currentValue = isDefaultOrEmpty(value)
          ? emptyValue
          : value;
    }

    public String getDefaultValue() {return this.defaultValue;}

    public boolean isCustomized() {return !isDefaultOrEmpty(this.currentValue);}

    @NotNull
    public String getActualValue() {

        return isCustomized()
          ? this.currentValue
          : this.defaultValue;
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.currentValue, this.defaultValue);
    }

    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        var that = (StringWithDefault) other;
        return Objects.equals(this.currentValue, that.currentValue) && Objects.equals(this.defaultValue, that.defaultValue);
    }

    @Override
    public String toString() {

        return this.defaultValue;
    }

    private boolean isDefaultOrEmpty(@NotNull String value) {

        return value.equals(emptyValue) || value.equals(this.defaultValue);
    }
}
