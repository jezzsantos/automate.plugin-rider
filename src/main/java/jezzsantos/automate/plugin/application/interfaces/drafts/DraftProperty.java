package jezzsantos.automate.plugin.application.interfaces.drafts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DraftProperty {

    private final String name;
    private final DraftElementValue value;

    public DraftProperty(@NotNull String name, @NotNull DraftElementValue value) {

        this.name = name;
        this.value = value;
    }

    @NotNull
    public String getName() {

        return this.name;
    }

    @Nullable
    public String getValue() {

        return this.value.getValue();
    }

    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        DraftProperty that = (DraftProperty) other;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.name);
    }
}
