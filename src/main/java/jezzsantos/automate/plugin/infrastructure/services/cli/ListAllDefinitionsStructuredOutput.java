package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ListAllDefinitions {
    public List<PatternLite> Patterns;
    public List<ToolkitLite> Toolkits;
    public List<DraftLite> Drafts;
}

public class ListAllDefinitionsStructuredOutput extends StructuredOutput<ListAllDefinitions> {
    public AllStateLite getAll() {
        return new AllStateLite(Objects.requireNonNullElse(this.Output.get(0).Values.Patterns, new ArrayList<>()), Objects.requireNonNullElse(this.Output.get(1).Values.Toolkits, new ArrayList<>()), Objects.requireNonNullElse(this.Output.get(2).Values.Drafts, new ArrayList<>()));
    }
}
