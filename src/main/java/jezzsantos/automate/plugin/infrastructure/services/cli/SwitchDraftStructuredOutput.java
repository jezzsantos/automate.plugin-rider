package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;

class SwitchDraft {
    public String Name;
    public String DraftId;

    public String ToolkitName;
    public String ToolkitId;
    public String Version;
}

public class SwitchDraftStructuredOutput extends StructuredOutput<SwitchDraft> {
    @SuppressWarnings("UnusedReturnValue")
    public DraftDefinition getDraft() {
        var values = this.Output.get(0).Values;
        return new DraftDefinition(values.DraftId, values.Name, values.ToolkitId, values.Version, true);
    }
}
