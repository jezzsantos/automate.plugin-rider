package jezzsantos.automate.plugin.application.interfaces.drafts;

import jezzsantos.automate.core.AutomateConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class DraftElement {

    @NotNull
    private final Map<String, DraftElementValue> map;
    @NotNull
    private final String name;
    private final boolean isRoot;

    public DraftElement(@NotNull String name, @NotNull Map<String, DraftElementValue> map, boolean isRoot) {

        this.isRoot = isRoot;
        this.name = name;
        this.map = map;
    }

    public static Map<String, DraftElementValue> toElementValueMap(@NotNull Map<String, Object> objectMap) {

        return objectMap.entrySet().stream()
          .collect(Collectors.toMap(Map.Entry::getKey, entry -> parseObjectValue(entry.getKey(), entry.getValue())));
    }

    public boolean containsKey(@NotNull String key) {

        return this.map.containsKey(key);
    }

    @Nullable
    public DraftProperty getProperty(@NotNull String name) {

        var value = this.map.get(name);
        if (value == null) {
            return null;
        }

        return new DraftProperty(name, value);
    }

    @Nullable
    public String getId() {

        if (!this.map.containsKey("Id")) {
            return null;
        }

        return this.map.get("Id").getValue();
    }

    @Nullable
    public String getConfigurePath() {

        if (!this.map.containsKey("ConfigurePath")) {
            return null;
        }

        return this.map.get("ConfigurePath").getValue();
    }

    @Nullable
    public String getSchemaId() {

        if (!this.map.containsKey("Schema")) {
            return null;
        }

        return Objects.requireNonNull(this.map.get("Schema").getElement()).getId();
    }

    public boolean isSchemaType(@NotNull AutomateConstants.SchemaType type) {

        if (!this.map.containsKey("Schema")) {
            return false;
        }

        var schemaType = Objects.requireNonNull(this.map.get("Schema").getElement()).getProperty("Type");
        if (schemaType == null) {
            return false;
        }

        return type.getValue().equals(schemaType.getValue());
    }

    @NotNull
    public ElementValueMap getProperties() {

        return new ElementValueMap(this.map.entrySet().stream()
                                     .filter(entry -> entry.getValue().isProperty()
                                       && !entry.getKey().equalsIgnoreCase("Id")
                                       && !entry.getKey().equalsIgnoreCase("ConfigurePath")
                                       && !entry.getKey().equalsIgnoreCase("Schema")
                                       && !entry.getKey().equalsIgnoreCase("Items"))
                                     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (val1, val2) -> val1, TreeMap::new)));
    }

    @Nullable
    public DraftElement getElement(@NotNull String name) {

        if (!this.map.containsKey(name)) {
            return null;
        }

        return this.map.get(name).getElement();
    }

    @NotNull
    public List<DraftElement> getCollectionItems() {

        if (!this.map.containsKey("Items")) {
            return new ArrayList<>();
        }

        var items = this.map.get("Items");
        return items.getCollectionItems();
    }

    @NotNull
    public ElementMap getCollections() {

        //noinspection ConstantConditions
        return new ElementMap(this.map.entrySet().stream()
                                .filter(entry -> entry.getValue().isCollection())
                                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getCollection(), (val1, val2) -> val1, TreeMap::new)));
    }

    @NotNull
    public ElementMap getElements() {

        //noinspection ConstantConditions
        return new ElementMap(this.map.entrySet().stream()
                                .filter(entry -> entry.getValue().isElement()
                                  && !entry.getKey().equalsIgnoreCase("Schema"))
                                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getElement())));
    }

    @NotNull
    public String getName() {

        return this.name;
    }

    public void deleteElement(@NotNull String elementId) {

        var element = this.getElements().entrySet().stream()
          .filter(ele -> Objects.requireNonNull(ele.getValue().getId()).equals(elementId))
          .findFirst();
        if (element.isEmpty()) {
            return;
        }

        var key = element.get().getKey();
        this.map.remove(key);
    }

    public void deleteDescendantCollectionItem(@NotNull String collectionItemId) {

        for (var collection : this.getCollections().entrySet()) {

            var collectionItems = collection.getValue().getCollectionItems();
            var collectionItem = collectionItems.stream()
              .filter(item -> Objects.requireNonNull(item.getId()).equals(collectionItemId))
              .findFirst();
            if (collectionItem.isEmpty()) {
                continue;
            }

            collection.getValue().deleteCollectionItem(collectionItem.get());
            break;
        }
    }

    public void deleteCollectionItem(@NotNull DraftElement collectionItem) {

        if (!this.map.containsKey("Items")) {
            return;
        }

        var items = this.map.get("Items");
        items.deleteCollectionItem(collectionItem);
    }

    public boolean isNotRoot() {

        return !this.isRoot;
    }

    public void addElement(@NotNull DraftElement element) {

        this.map.put(element.name, new DraftElementValue(element.name, element.map));
    }

    public void addProperty(DraftProperty property) {

        this.map.put(property.getName(), new DraftElementValue(property.getValue()));
    }

    public int indexOf(@NotNull DraftElement childElement) {

        return this.getElements().indexOf(childElement);
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getId());
    }

    @Override
    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        var that = (DraftElement) other;
        var thatId = that.getId();
        var thisId = this.getId();
        if (thisId == null || thatId == null) {
            return false;
        }

        return Objects.equals(thisId, thatId);
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private static DraftElementValue parseObjectValue(@NotNull String name, @NotNull Object value) {

        if (value instanceof Boolean) {
            return new DraftElementValue(value.toString());
        }

        if (value instanceof Double) {
            double temp = (Double) value;
            if (temp % 1 == 0) {
                return new DraftElementValue(Integer.toString((int) temp));
            }
            return new DraftElementValue(Double.toString(temp));
        }

        if (value instanceof Map) {
            var objectMap = (Map<String, Object>) value;
            var itemMap = new HashMap<String, DraftElementValue>();
            objectMap
              .forEach((key, val) -> {
                  var listItemName = isItemsList(key, val)
                    ? name
                    : key;
                  itemMap.put(key, parseObjectValue(listItemName, val));
              });
            return new DraftElementValue(name, itemMap);
        }

        if (value instanceof ArrayList<?>) {
            var objectMapList = (List<Map<String, Object>>) value;
            var elementList = new ArrayList<DraftElement>();
            objectMapList.stream()
              .filter(Objects::nonNull)
              .forEach(map -> {
                  var properties = map.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> parseObjectValue(entry.getKey(), entry.getValue())));
                  elementList.add(new DraftElement(name, properties, false));
              });

            return new DraftElementValue(elementList);
        }

        return new DraftElementValue(value.toString());
    }

    private static boolean isItemsList(String key, Object value) {

        return (value instanceof ArrayList<?>) && (key.equals("Items"));
    }
}
