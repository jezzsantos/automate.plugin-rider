package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ListDrafts {
    public List<DraftDefinition> Drafts;
}

public class ListDraftsStructuredOutput extends StructuredOutput<ListDrafts> {
    public List<DraftDefinition> getDrafts() {
        return Objects.requireNonNullElse(this.Output.get(0).Values.Drafts, new ArrayList<>());
    }
}
