package jezzsantos.automate.plugin.infrastructure.common;


import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;

class CreatePattern {
    public String Name;
    public String PatternId;
}

public class CreatePatternStructuredOutput extends StructuredOutput<CreatePattern> {
    public PatternDefinition getPattern() {
        var values = this.Output.get(0).Values;
        return new PatternDefinition(values.PatternId, values.Name, "0.0.0", true);
    }
}
