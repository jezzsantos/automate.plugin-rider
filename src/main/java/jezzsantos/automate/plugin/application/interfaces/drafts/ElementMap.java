package jezzsantos.automate.plugin.application.interfaces.drafts;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ElementMap implements Iterable<DraftElement> {

    private final Map<String, DraftElement> map;

    public ElementMap(Map<String, DraftElement> map) {

        this.map = map;
    }

    public DraftElement get(String name) {

        return this.map.get(name);
    }

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

    @NotNull
    @Override
    public Iterator<DraftElement> iterator() {

        return this.map.values().iterator();
    }
}
