package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElement;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElementSchema;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DraftElementPlaceholderNode {

    @NotNull
    private final DraftElement element;
    @NotNull
    private final String name;
    @NotNull
    private final PatternElement pattern;
    private final boolean isCollectionItem;

    public DraftElementPlaceholderNode(@NotNull PatternElement pattern, @NotNull DraftElement element, boolean isCollectionItem, @NotNull String displayName) {

        this.pattern = pattern;
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

    @Nullable
    public DraftElementSchema getSchema() {

        var schemaId = this.element.getSchemaId();
        if (schemaId == null) {
            return null;
        }

        return this.pattern.findSchema(schemaId);
    }
}
