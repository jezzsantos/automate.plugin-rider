package jezzsantos.automate.plugin.infrastructure.common.cli;

import jezzsantos.automate.plugin.application.interfaces.ToolkitDefinition;

import java.util.List;

class ListToolkits {
    public List<ToolkitDefinition> Toolkits;
}

public class ListToolkitsStructuredOutput extends StructuredOutput<ListToolkits> {
    public List<ToolkitDefinition> getToolkits() {
        return this.Output.get(0).Values.Toolkits;
    }
}
