package jezzsantos.automate.plugin.infrastructure.services.cli.responses.patterns;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternDetailed;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternVersion;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutput;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutputOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

public class GetUpdatePatternStructuredOutput extends StructuredOutput<PatternDetailed> {

    @UsedImplicitly
    public GetUpdatePatternStructuredOutput() {

    }

    @SuppressWarnings("unused")
    @TestOnly
    public GetUpdatePatternStructuredOutput(@NotNull String id, @NotNull String name, @NotNull PatternVersion version, @NotNull PatternElement pattern) {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new PatternDetailed(id, name, version, pattern);
        }})));
    }

    public PatternDetailed getPattern() {

        return this.Output.get(0).Values;
    }
}
