package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElement;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
class AddRemoveElement {

    public String DraftName;
    public String DraftItemId;
    public Map<String, Object> Configuration;
}

public class AddRemoveDraftElementStructuredOutput extends StructuredOutput<AddRemoveElement> {

    @TestOnly
    public AddRemoveDraftElementStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new AddRemoveElement();
        }})));
    }

    public DraftElement getElement() {

        var values = this.Output.get(0).Values;
        return new DraftElement(values.DraftName, new HashMap<>(), false);
    }
}
