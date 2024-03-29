package jezzsantos.automate.plugin.infrastructure.services.cli.responses.drafts;

import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutput;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutputOutput;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
class DeletedDraft {

    public String DraftName;
    public String DraftId;
}

public class DeleteDraftStructuredOutput extends StructuredOutput<DeletedDraft> {

    @TestOnly
    public DeleteDraftStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new DeletedDraft();
        }})));
    }
}
