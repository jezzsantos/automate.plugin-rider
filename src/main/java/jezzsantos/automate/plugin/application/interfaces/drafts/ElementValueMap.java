package jezzsantos.automate.plugin.application.interfaces.drafts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ElementValueMap implements Iterable<DraftProperty> {

    private final Map<String, DraftElementValue> map;

    public ElementValueMap(@NotNull Map<String, DraftElementValue> map) {

        this.map = map;
    }

    @Nullable
    public DraftProperty get(int index) {

        var key = (String) this.map.keySet().toArray()[index];
        return this.get(key);
    }

    @Nullable
    public DraftProperty get(@NotNull String name) {

        var value = this.map.get(name);
        if (value == null) {
            return null;
        }

        return new DraftProperty(name, value);
    }

    public int size() {

        return this.map.size();
    }

    public boolean isEmpty() {

        return this.map.isEmpty();
    }

    @NotNull
    @Override
    public Iterator<DraftProperty> iterator() {

        return new Iterator<>() {

            private final Iterator<String> iterator = ElementValueMap.this.map.keySet().iterator();

            @Override
            public boolean hasNext() {

                return this.iterator.hasNext();
            }

            @Override
            public DraftProperty next() {

                var name = this.iterator.next();
                var value = ElementValueMap.this.map.get(name);
                return new DraftProperty(name, value);
            }
        };
    }

    public ElementValueMap sortedByName() {

        return new ElementValueMap(this.map.entrySet().stream()
                                     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                                               (val1, val2) -> val1, TreeMap::new)));
    }
}
