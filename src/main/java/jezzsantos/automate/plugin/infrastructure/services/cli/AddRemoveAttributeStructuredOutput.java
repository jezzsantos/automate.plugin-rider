package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;

@SuppressWarnings("unused")
class AddRemoveAttribute {

    public String Name;
    public String AttributeId;
    public String ParentId;
}

public class AddRemoveAttributeStructuredOutput extends StructuredOutput<AddRemoveAttribute> {

    public Attribute getAttribute() {
        var values = this.Output.get(0).Values;
        return new Attribute(values.AttributeId, values.Name);
    }
}
