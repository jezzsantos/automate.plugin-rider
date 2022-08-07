package jezzsantos.automate.data;

public class PatternDefinition {
    private final String id;
    private final String name;

    public PatternDefinition(String id, String name) {
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
