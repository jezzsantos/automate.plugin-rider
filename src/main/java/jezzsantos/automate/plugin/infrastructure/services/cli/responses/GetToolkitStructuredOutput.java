package jezzsantos.automate.plugin.infrastructure.services.cli.responses;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitDetailed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

public class GetToolkitStructuredOutput extends StructuredOutput<ToolkitDetailed> {

    @UsedImplicitly
    public GetToolkitStructuredOutput() {

    }

    @SuppressWarnings("unused")
    @TestOnly
    public GetToolkitStructuredOutput(@NotNull String id, @NotNull String name, @NotNull String version, @NotNull String runtimeVersion, @NotNull PatternElement pattern) {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new ToolkitDetailed(id, name, version, runtimeVersion, pattern);
        }})));
    }

    public ToolkitDetailed getToolkit() {

        return this.Output.get(0).Values;
    }
}
