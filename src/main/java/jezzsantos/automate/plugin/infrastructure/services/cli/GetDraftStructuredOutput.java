package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftDetailed;

class GetDraft {
    public DraftDetailed Draft;
}

public class GetDraftStructuredOutput extends StructuredOutput<GetDraft> {
    public DraftDetailed getDraft() {
        return this.Output.get(0).Values.Draft;
    }
}
