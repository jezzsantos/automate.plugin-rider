package jezzsantos.automate.plugin.infrastructure.services.cli.responses;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;

import java.util.ArrayList;
import java.util.List;

class ListToolkits {

    public List<ToolkitLite> Toolkits = new ArrayList<>();
}

public class ListToolkitsStructuredOutput extends StructuredOutput<ListToolkits> {

    @UsedImplicitly
    public ListToolkitsStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new ListToolkits();
        }})));
    }

    public List<ToolkitLite> getToolkits() {

        return this.Output.get(0).Values.Toolkits;
    }
}
