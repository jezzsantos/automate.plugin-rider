package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.patterns.PatternDetailed;

public class GetPatternStructuredOutput extends StructuredOutput<PatternDetailed> {

    public PatternDetailed getPattern() {
        return this.Output.get(0).Values;
    }
}
