package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class ListDrafts {
    public List<DraftLite> Drafts;
}

public class ListDraftsStructuredOutput extends StructuredOutput<ListDrafts> {
    public List<DraftLite> getDrafts() {
        return Objects.requireNonNullElse(this.Output.get(0).Values.Drafts, new ArrayList<>());
    }
}
