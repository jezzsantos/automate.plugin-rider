package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternDetailed;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

public class GetPatternStructuredOutput extends StructuredOutput<PatternDetailed> {

    @UsedImplicitly
    public GetPatternStructuredOutput() {
    }

    @SuppressWarnings("unused")
    @TestOnly
    public GetPatternStructuredOutput(@NotNull String id, @NotNull String name, @NotNull String version, @NotNull PatternElement pattern) {
        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            Values = new PatternDetailed(id, name, version, pattern);
        }})));
    }

    public PatternDetailed getPattern() {
        return this.Output.get(0).Values;
    }
}
