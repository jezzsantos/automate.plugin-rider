package jezzsantos.automate.plugin.infrastructure.services.cli.responses;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;

import java.util.ArrayList;
import java.util.List;

class ListDrafts {

    public List<DraftLite> Drafts = new ArrayList<>();
}

public class ListDraftsStructuredOutput extends StructuredOutput<ListDrafts> {

    @UsedImplicitly
    public ListDraftsStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new ListDrafts();
        }})));
    }

    public List<DraftLite> getDrafts() {

        return this.Output.get(0).Values.Drafts;
    }
}
