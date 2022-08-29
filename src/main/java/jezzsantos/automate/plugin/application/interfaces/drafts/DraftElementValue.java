package jezzsantos.automate.plugin.application.interfaces.drafts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class DraftElementValue {

    @Nullable
    private String property;
    @Nullable
    private DraftElement element;
    @Nullable
    private List<DraftElement> collectionItems;

    public DraftElementValue(@NotNull String property) {

        this.property = property;
    }

    public DraftElementValue(@NotNull String name, @NotNull Map<String, DraftElementValue> element) {

        this.element = new DraftElement(name, element, false);
    }

    public DraftElementValue(@NotNull List<DraftElement> collectionItems) {

        this.collectionItems = collectionItems;
    }

    public boolean isProperty() {

        return this.property != null
          && !isElement()
          && !isCollection();
    }

    public boolean isElement() {

        return this.element != null
          && !this.element.containsKey("Items")
          && !isProperty();
    }

    public boolean isCollection() {

        return this.element != null
          && this.element.containsKey("Items")
          && !isProperty();
    }

    public boolean hasCollectionItems() {

        return this.collectionItems != null
          && !isElement()
          && !isProperty();
    }

    @Nullable
    public String getValue() {

        if (!isProperty()) {
            return null;
        }

        return this.property;
    }

    @Nullable
    public DraftElement getCollection() {

        if (!isCollection()) {
            return null;
        }

        return this.element;
    }

    @Nullable
    public DraftElement getElement() {

        if (!isElement()) {
            return null;
        }

        return this.element;
    }

    @NotNull
    public List<DraftElement> getCollectionItems() {

        if (!hasCollectionItems()) {
            return new ArrayList<>();
        }

        assert this.collectionItems != null;
        //noinspection ConstantConditions
        return Objects.requireNonNull(this.collectionItems.stream()
                                        .sorted(Comparator.comparing(DraftElement::getId))
                                        .collect(Collectors.toList()));
    }
}
