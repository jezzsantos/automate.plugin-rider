package jezzsantos.automate.plugin.application.interfaces.drafts;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DraftProperty {

    private final String name;
    private final DraftElementValue value;

    public DraftProperty(@NotNull String name, @NotNull DraftElementValue value) {

        this.name = name;
        this.value = value;
    }

    public String getName() {

        return this.name;
    }

    public String getValue() {

        return this.value.getValue();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DraftProperty that = (DraftProperty) o;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.name);
    }
}
