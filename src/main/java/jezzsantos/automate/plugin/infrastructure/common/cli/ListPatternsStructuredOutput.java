package jezzsantos.automate.plugin.infrastructure.common.cli;

import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;

import java.util.List;

class ListPatterns {
    public List<PatternDefinition> Patterns;
}

public class ListPatternsStructuredOutput extends StructuredOutput<ListPatterns> {
    public List<PatternDefinition> getPatterns() {
        return this.Output.get(0).Values.Patterns;
    }
}
