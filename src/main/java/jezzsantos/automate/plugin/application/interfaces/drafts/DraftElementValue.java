package jezzsantos.automate.plugin.application.interfaces.drafts;

import jezzsantos.automate.core.AutomateConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class DraftElementValue {

    @NotNull
    private final DraftElementValueType elementValueType;
    @Nullable
    private String property;
    @Nullable
    private DraftElement element;
    @Nullable
    private List<DraftElement> collectionItems;

    public DraftElementValue(@Nullable String property) {

        this.elementValueType = DraftElementValueType.PROPERTY;
        this.property = property;
    }

    public DraftElementValue(@NotNull String name, @NotNull Map<String, DraftElementValue> element) {

        this.element = new DraftElement(name, element, false);
        this.elementValueType = detectCollection(this.element)
          ? DraftElementValueType.COLLECTION
          : DraftElementValueType.ELEMENT;
    }

    public DraftElementValue(@NotNull List<DraftElement> collectionItems) {

        this.elementValueType = DraftElementValueType.ELEMENT;
        this.collectionItems = collectionItems;
    }

    public boolean isProperty() {

        return this.elementValueType == DraftElementValueType.PROPERTY;
    }

    public boolean isElement() {

        return this.elementValueType == DraftElementValueType.ELEMENT;
    }

    public boolean isCollection() {

        return this.elementValueType == DraftElementValueType.COLLECTION;
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

        return Objects.requireNonNull(Objects.requireNonNull(this.collectionItems).stream()
                                        .sorted(Comparator.comparing(item -> Objects.requireNonNull(item.getId())))
                                        .collect(Collectors.toList()));
    }

    public void deleteCollectionItem(@NotNull DraftElement collectionItem) {

        if (!hasCollectionItems()) {
            return;
        }

        Objects.requireNonNull(this.collectionItems).remove(collectionItem);
    }

    private static boolean detectCollection(DraftElement element) {

        var containsItemsCollection = element.containsKey("Items");
        var hasCollectionSchema = element.containsKey("Schema")
          && element.isSchemaType(AutomateConstants.SchemaType.EPHEMERALCOLLECTION);

        return containsItemsCollection || hasCollectionSchema;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean hasCollectionItems() {

        return isElement()
          && this.collectionItems != null;
    }

    private enum DraftElementValueType {
        PROPERTY,
        ELEMENT,
        COLLECTION
    }
}
