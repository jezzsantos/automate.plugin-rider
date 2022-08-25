package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElement;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftProperty;
import org.jetbrains.annotations.NotNull;

class DraftElementPlaceholderNode {

    @NotNull
    private final DraftElement element;
    @NotNull
    private final String name;
    private final boolean isCollectionItem;

    public DraftElementPlaceholderNode(@NotNull DraftElement element, boolean isCollectionItem, @NotNull String displayName) {

        this.name = displayName;
        this.isCollectionItem = isCollectionItem;
        this.element = element;
    }

    @NotNull
    public DraftElement getElement() {

        return this.element;
    }

    @Override
    public String toString() {

        return this.name;
    }

    public boolean isCollectionItem() {

        return this.isCollectionItem;
    }
}

class DraftPropertyPlaceholderNode {

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