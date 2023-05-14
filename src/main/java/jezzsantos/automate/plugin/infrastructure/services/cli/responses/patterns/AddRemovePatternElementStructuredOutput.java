package jezzsantos.automate.plugin.infrastructure.services.cli.responses.patterns;

import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutput;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutputOutput;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
class AddRemovePatternElement {

    public String Name;
    public String ElementId;
    public String ParentId;
    public String EditPath;
    public String Description;
    public String DisplayName;
    public boolean AutoCreate;
    public AutomateConstants.ElementCardinality Cardinality;
    public boolean IsCollection;
}

public class AddRemovePatternElementStructuredOutput extends StructuredOutput<AddRemovePatternElement> {

    @TestOnly
    public AddRemovePatternElementStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new AddRemovePatternElement();
        }})));
    }

    public PatternElement getElement() {

        var values = this.Output.get(0).Values;
        return new PatternElement(values.ElementId, values.Name, values.EditPath, values.DisplayName, values.Description, values.Cardinality, values.AutoCreate);
    }
}

