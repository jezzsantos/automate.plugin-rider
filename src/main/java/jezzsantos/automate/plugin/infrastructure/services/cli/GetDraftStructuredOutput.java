package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftDetailed;

public class GetDraftStructuredOutput extends StructuredOutput<DraftDetailed> {

    public DraftDetailed getDraft() {
        return this.Output.get(0).Values;
    }
}
