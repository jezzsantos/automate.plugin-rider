package jezzsantos.automate.plugin.infrastructure.common.cli;

import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ListPatterns {
    public List<PatternDefinition> Patterns;
}

public class ListPatternsStructuredOutput extends StructuredOutput<ListPatterns> {
    public List<PatternDefinition> getPatterns() {
        return Objects.requireNonNullElse(this.Output.get(0).Values.Patterns, new ArrayList<>());
    }
}
