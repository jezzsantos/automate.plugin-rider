package jezzsantos.automate.plugin.infrastructure.services.cli.responses;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElement;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
class AddRemoveDraftElement {

    public String Name;
    public String DraftItemId;
    public Map<String, Object> Configuration;
}

public class AddRemoveDraftElementStructuredOutput extends StructuredOutput<AddRemoveDraftElement> {

    @TestOnly
    public AddRemoveDraftElementStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new AddRemoveDraftElement();
        }})));
    }

    public DraftElement getElement() {

        var values = this.Output.get(0).Values;
        return new DraftElement(values.Name, DraftElement.toElementValueMap(values.Configuration), false);
    }
}

