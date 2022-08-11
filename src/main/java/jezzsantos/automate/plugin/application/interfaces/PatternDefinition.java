package jezzsantos.automate.plugin.application.interfaces;

public class PatternDefinition {
    public String Id;
    public String Name;
    public String Version;
    public Boolean IsCurrent;

    public PatternDefinition(String id, String name, String version, Boolean isCurrent) {
        this.Id = id;
        this.Name = name;
        this.Version = version;
        this.IsCurrent = isCurrent;
    }

    @Override
    public String toString() {
        return String.format("%s  (%s)", this.Name, this.Id);
    }
}
