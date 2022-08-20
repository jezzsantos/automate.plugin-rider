package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.patterns.PatternDetailed;

class GetPattern {
    public PatternDetailed Pattern;
}

public class GetPatternStructuredOutput extends StructuredOutput<GetPattern> {
    public PatternDetailed getPattern() {
        return this.Output.get(0).Values.Pattern;
    }
}
