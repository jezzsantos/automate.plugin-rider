package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;

import java.util.ArrayList;
import java.util.List;

class ListPatterns {

    public List<PatternLite> Patterns = new ArrayList<>();
}

public class ListPatternsStructuredOutput extends StructuredOutput<ListPatterns> {

    @UsedImplicitly
    public ListPatternsStructuredOutput() {
        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            Values = new ListPatterns();
        }})));
    }

    public List<PatternLite> getPatterns() {
        return this.Output.get(0).Values.Patterns;
    }
}
