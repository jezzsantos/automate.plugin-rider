package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;

import java.util.ArrayList;
import java.util.List;

class ListAllDefinitions {

    public List<PatternLite> Patterns = new ArrayList<>();
    public List<ToolkitLite> Toolkits = new ArrayList<>();
    public List<DraftLite> Drafts = new ArrayList<>();
}

public class ListAllDefinitionsStructuredOutput extends StructuredOutput<ListAllDefinitions> {

    @UsedImplicitly
    public ListAllDefinitionsStructuredOutput() {
        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            Values = new ListAllDefinitions();
        }}, new StructuredOutputOutput<>() {{
            Values = new ListAllDefinitions();
        }}, new StructuredOutputOutput<>() {{
            Values = new ListAllDefinitions();
        }})));
    }

    public AllStateLite getAll() {
        return new AllStateLite(this.Output.get(0).Values.Patterns, this.Output.get(1).Values.Toolkits, this.Output.get(2).Values.Drafts);
    }
}
