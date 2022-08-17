package jezzsantos.automate.plugin.infrastructure.services.cli;


import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;

class CreatePattern {
    public String Name;
    public String PatternId;
    public String Version;
}

public class CreatePatternStructuredOutput extends StructuredOutput<CreatePattern> {
    public PatternDefinition getPattern() {
        var values = this.Output.get(0).Values;
        return new PatternDefinition(values.PatternId, values.Name, values.Version, true);
    }
}
