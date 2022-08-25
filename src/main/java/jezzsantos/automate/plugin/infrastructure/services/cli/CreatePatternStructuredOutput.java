package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;

import java.util.ArrayList;
import java.util.List;

class CreatePattern {

    public String Name;
    public String PatternId;
    public String Version;
}

public class CreatePatternStructuredOutput extends StructuredOutput<CreatePattern> {

    @UsedImplicitly
    public CreatePatternStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new CreatePattern();
        }})));
    }

    public PatternLite getPattern() {

        var values = this.Output.get(0).Values;
        return new PatternLite(values.PatternId, values.Name, values.Version, true);
    }
}
