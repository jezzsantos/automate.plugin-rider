package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
class AddRemoveAttribute {

    public String Name;
    public String AttributeId;
    public String ParentId;
}

public class AddRemoveAttributeStructuredOutput extends StructuredOutput<AddRemoveAttribute> {

    @TestOnly
    public AddRemoveAttributeStructuredOutput() {
        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            Values = new AddRemoveAttribute();
        }})));
    }

    public Attribute getAttribute() {
        var values = this.Output.get(0).Values;
        return new Attribute(values.AttributeId, values.Name);
    }
}
