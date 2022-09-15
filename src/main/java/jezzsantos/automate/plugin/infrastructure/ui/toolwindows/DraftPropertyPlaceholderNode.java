package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DraftPropertyPlaceholderNode {

    @NotNull
    private final DraftProperty property;
    @NotNull
    private final String name;

    public DraftPropertyPlaceholderNode(@NotNull DraftProperty property) {

        this.name = String.format("%s: %s", property.getName(), property.getValue());
        this.property = property;
    }

    @NotNull
    public DraftProperty getProperty() {

        return this.property;
    }

    @Override
    public String toString() {

        return this.name;
    }

    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        var that = (DraftPropertyPlaceholderNode) other;
        return this.property.equals(that.property);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.property);
    }
}