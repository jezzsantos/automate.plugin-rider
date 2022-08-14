package jezzsantos.automate.plugin.infrastructure.common.cli;

import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;

import java.util.List;

class ListDrafts {
    public List<DraftDefinition> Drafts;
}

public class ListDraftsStructuredOutput extends StructuredOutput<ListDrafts> {
    public List<DraftDefinition> getDrafts() {
        return this.Output.get(0).Values.Drafts;
    }
}
