package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;

class SwitchPattern {
    public String Name;
    public String PatternId;
    public String Version;
}

public class SwitchPatternStructuredOutput extends StructuredOutput<SwitchPattern> {
    @SuppressWarnings("UnusedReturnValue")
    public PatternLite getPattern() {
        var values = this.Output.get(0).Values;
        return new PatternLite(values.PatternId, values.Name, values.Version, true);
    }
}
