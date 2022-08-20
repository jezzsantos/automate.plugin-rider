package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ListPatterns {
    public List<PatternLite> Patterns;
}

public class ListPatternsStructuredOutput extends StructuredOutput<ListPatterns> {
    public List<PatternLite> getPatterns() {
        return Objects.requireNonNullElse(this.Output.get(0).Values.Patterns, new ArrayList<>());
    }
}
