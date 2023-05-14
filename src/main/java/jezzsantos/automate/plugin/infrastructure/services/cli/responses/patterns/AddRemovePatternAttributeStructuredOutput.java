package jezzsantos.automate.plugin.infrastructure.services.cli.responses.patterns;

import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutput;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutputOutput;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
class AddRemoveAttribute {

    public String Name;
    public String AttributeId;
    public String ParentId;
    public boolean IsRequired;
    public AutomateConstants.AttributeDataType DataType;
    public String DefaultValue;
    public List<String> Choices;
}

public class AddRemovePatternAttributeStructuredOutput extends StructuredOutput<AddRemoveAttribute> {

    @TestOnly
    public AddRemovePatternAttributeStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new AddRemoveAttribute();
        }})));
    }

    public Attribute getAttribute() {

        var values = this.Output.get(0).Values;
        return new Attribute(values.AttributeId, values.Name, values.IsRequired, values.DefaultValue, values.DataType, values.Choices);
    }
}
