package jezzsantos.automate.plugin.application.interfaces.drafts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class DraftElement {

    @NotNull
    private final Map<String, DraftElementValue> map;
    @NotNull
    private final String name;

    public DraftElement(@NotNull String name, @NotNull Map<String, DraftElementValue> map) {

        this.name = name;
        this.map = map;
    }

    public static Map<String, DraftElementValue> toElementValueMap(@NotNull Map<String, Object> objectMap) {

        return objectMap.entrySet().stream()
          .collect(Collectors.toMap(Map.Entry::getKey, entry -> getObjectValue(entry.getKey(), entry.getValue())));
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

    @NotNull
    public ElementValueMap getProperties() {

        return new ElementValueMap(this.map.entrySet().stream()
                                     .filter(prop -> prop.getValue().isProperty()
                                       && !prop.getKey().equalsIgnoreCase("Id")
                                       && !prop.getKey().equalsIgnoreCase("Items"))
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
                                .filter(entry -> entry.getValue().isElement())
                                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getElement())));
    }

    @NotNull
    public String getName() {

        return this.name;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DraftElement that = (DraftElement) o;
        return Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.getId());
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private static DraftElementValue getObjectValue(@NotNull String name, @NotNull Object value) {

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
              .forEach((key, val) -> itemMap.put(key, getObjectValue(key, val)));
            return new DraftElementValue(name, itemMap);
        }

        if (value instanceof ArrayList<?>) {
            var objectMapList = (List<Map<String, Object>>) value;
            var elementList = new ArrayList<DraftElement>();
            objectMapList.stream()
              .filter(Objects::nonNull)
              .forEach(map -> elementList.add(new DraftElement(name,
                                                               map.entrySet().stream()
                                                                 .collect(Collectors.toMap(Map.Entry::getKey, entry -> getObjectValue(entry.getKey(), entry.getValue()))))));

            return new DraftElementValue(elementList);
        }

        return new DraftElementValue(value.toString());
    }
}
