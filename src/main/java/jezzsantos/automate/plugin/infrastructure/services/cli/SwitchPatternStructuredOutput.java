package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;

import java.util.ArrayList;
import java.util.List;

class SwitchPattern {

    public String Name;
    public String PatternId;
    public String Version;
}

public class SwitchPatternStructuredOutput extends StructuredOutput<SwitchPattern> {

    @UsedImplicitly
    public SwitchPatternStructuredOutput() {
        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            Values = new SwitchPattern();
        }})));
    }

    @SuppressWarnings("UnusedReturnValue")
    public PatternLite getPattern() {
        var values = this.Output.get(0).Values;
        return new PatternLite(values.PatternId, values.Name, values.Version, true);
    }
}
