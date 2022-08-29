package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftProperty;
import org.jetbrains.annotations.NotNull;

public class DraftPropertyPlaceholderNode {

    @NotNull
    private final DraftProperty property;
    @NotNull
    private final String name;

    public DraftPropertyPlaceholderNode(@NotNull DraftProperty property, @NotNull String displayName) {

        this.name = displayName;
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
}