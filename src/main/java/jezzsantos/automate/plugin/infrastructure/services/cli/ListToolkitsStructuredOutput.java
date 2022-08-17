package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.ToolkitDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ListToolkits {
    public List<ToolkitDefinition> Toolkits;
}

public class ListToolkitsStructuredOutput extends StructuredOutput<ListToolkits> {
    public List<ToolkitDefinition> getToolkits() {
        return Objects.requireNonNullElse(this.Output.get(0).Values.Toolkits, new ArrayList<>());
    }
}
