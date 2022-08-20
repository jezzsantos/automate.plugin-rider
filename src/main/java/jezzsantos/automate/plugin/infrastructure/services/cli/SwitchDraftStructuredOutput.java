package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;

class SwitchDraft {
    public String Name;
    public String DraftId;

    @SuppressWarnings("unused")
    public String ToolkitName;
    public String ToolkitId;
    public String Version;
}

public class SwitchDraftStructuredOutput extends StructuredOutput<SwitchDraft> {
    @SuppressWarnings("UnusedReturnValue")
    public DraftLite getDraft() {
        var values = this.Output.get(0).Values;
        return new DraftLite(values.DraftId, values.Name, values.ToolkitId, values.Version, true);
    }
}
