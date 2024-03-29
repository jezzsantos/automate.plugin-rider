package jezzsantos.automate.plugin.application.interfaces.drafts;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ElementMap implements Iterable<DraftElement> {

    private final Map<String, DraftElement> map;

    public ElementMap(Map<String, DraftElement> map) {

        this.map = map;
    }

    @Nullable
    public DraftElement get(@NotNull String name) {

        return this.map.get(name);
    }

    @Nullable
    public DraftElement get(int index) {

        var key = (String) this.map.keySet().toArray()[index];
        return this.get(key);
    }

    public int size() {

        return this.map.size();
    }

    public boolean isEmpty() {

        return this.map.isEmpty();
    }

    public Set<Map.Entry<String, DraftElement>> entrySet() {

        return this.map.entrySet();
    }

    public int indexOf(DraftElement childElement) {

        if (this.map.isEmpty()) {
            return -1;
        }

        var index = -1;
        for (var value : this.map.values()) {
            index++;
            if (value.equals(childElement)) {
                return index;
            }
        }
        return -1;
    }

    @NotNull
    @Override
    public Iterator<DraftElement> iterator() {

        return this.map.values().iterator();
    }
}
