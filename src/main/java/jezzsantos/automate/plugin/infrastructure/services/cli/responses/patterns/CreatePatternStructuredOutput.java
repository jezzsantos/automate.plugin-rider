package jezzsantos.automate.plugin.infrastructure.services.cli.responses.patterns;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternVersion;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutput;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutputOutput;

import java.util.ArrayList;
import java.util.List;

class CreatePattern {

    public String Name;
    public String PatternId;
    public PatternVersion Version;
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
