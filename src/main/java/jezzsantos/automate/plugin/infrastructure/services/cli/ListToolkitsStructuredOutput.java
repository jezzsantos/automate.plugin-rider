package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ListToolkits {
    public List<ToolkitLite> Toolkits;
}

public class ListToolkitsStructuredOutput extends StructuredOutput<ListToolkits> {
    public List<ToolkitLite> getToolkits() {
        return Objects.requireNonNullElse(this.Output.get(0).Values.Toolkits, new ArrayList<>());
    }
}
