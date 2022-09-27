package jezzsantos.automate.plugin.common;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class StringWithImplicitDefault {

    private static final String defaultValue = "";
    private final String implicitValue;
    private String currentValue;

    public StringWithImplicitDefault(@NotNull String implicitValue) {

        this.implicitValue = implicitValue;
        this.currentValue = defaultValue;
    }

    public StringWithImplicitDefault(@NotNull String implicitValue, @NotNull String currentValue) {

        this.implicitValue = implicitValue;
        this.currentValue = isImplicitOrDefault(currentValue)
          ? defaultValue
          : currentValue;
    }

    public static StringWithImplicitDefault fromValue(@NotNull String value) {

        return new StringWithImplicitDefault(value);
    }

    @NotNull
    public String getValue() {return this.currentValue;}

    public void setValue(@NotNull String value) {

        this.currentValue = isImplicitOrDefault(value)
          ? defaultValue
          : value;
    }

    public String getImplicitValue() {return this.implicitValue;}

    public boolean isCustomized() {return !isImplicitOrDefault(this.currentValue);}

    public StringWithImplicitDefault createCopyWithValue(@NotNull String value) {

        return new StringWithImplicitDefault(this.implicitValue, value);
    }

    @NotNull
    public String getExplicitValue() {

        return isCustomized()
          ? this.currentValue
          : this.implicitValue;
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.currentValue, this.implicitValue);
    }

    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        var that = (StringWithImplicitDefault) other;
        return Objects.equals(this.currentValue, that.currentValue) && Objects.equals(this.implicitValue, that.implicitValue);
    }

    @Override
    public String toString() {

        return this.implicitValue;
    }

    private boolean isImplicitOrDefault(@NotNull String value) {

        return value.equals(defaultValue) || value.equals(this.implicitValue);
    }
}
