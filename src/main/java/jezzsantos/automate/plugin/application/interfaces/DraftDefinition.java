package jezzsantos.automate.plugin.application.interfaces;

public class DraftDefinition {
    private final String id;
    private final String name;

    public DraftDefinition(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.format("%s  (%s)", this.name, this.id.substring(24));
    }
}
