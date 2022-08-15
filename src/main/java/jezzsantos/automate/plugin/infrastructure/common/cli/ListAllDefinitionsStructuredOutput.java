package jezzsantos.automate.plugin.infrastructure.common.cli;

import jezzsantos.automate.plugin.application.interfaces.AllDefinitions;
import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;
import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;
import jezzsantos.automate.plugin.application.interfaces.ToolkitDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ListAllDefinitions {
    public List<PatternDefinition> Patterns;
    public List<ToolkitDefinition> Toolkits;
    public List<DraftDefinition> Drafts;
}

public class ListAllDefinitionsStructuredOutput extends StructuredOutput<ListAllDefinitions> {
    public AllDefinitions getAll() {
        return new AllDefinitions(
                Objects.requireNonNullElse(this.Output.get(0).Values.Patterns, new ArrayList<>()),
                Objects.requireNonNullElse(this.Output.get(1).Values.Toolkits, new ArrayList<>()),
                Objects.requireNonNullElse(this.Output.get(2).Values.Drafts, new ArrayList<>()));
    }
}
