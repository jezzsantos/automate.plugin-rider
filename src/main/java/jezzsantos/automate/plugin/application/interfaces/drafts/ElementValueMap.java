package jezzsantos.automate.plugin.application.interfaces.drafts;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;

public class ElementValueMap implements Iterable<DraftProperty> {

    private final Map<String, DraftElementValue> map;

    public ElementValueMap(@NotNull Map<String, DraftElementValue> map) {

        this.map = map;
    }

    public DraftProperty get(String name) {

        return new DraftProperty(name, this.map.get(name));
    }

    public DraftProperty get(int index) {

        var key = (String) this.map.keySet().toArray()[index];
        return this.get(key);
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
            @Override
            public boolean hasNext() {

                return ElementValueMap.this.map.keySet().iterator().hasNext();
            }

            @Override
            public DraftProperty next() {

                var name = ElementValueMap.this.map.keySet().iterator().next();
                var value = ElementValueMap.this.map.get(name);
                return new DraftProperty(name, value);
            }
        };
    }
}
