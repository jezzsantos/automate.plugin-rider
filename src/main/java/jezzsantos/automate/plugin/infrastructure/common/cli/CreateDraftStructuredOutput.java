package jezzsantos.automate.plugin.infrastructure.common.cli;


import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;

class CreateDraft {
    public String Name;
    public String DraftId;

    public String ToolkitName;
    public String ToolkitId;
    public String Version;
}

public class CreateDraftStructuredOutput extends StructuredOutput<CreateDraft> {
    public DraftDefinition getDraft() {
        var values = this.Output.get(0).Values;
        return new DraftDefinition(values.DraftId, values.Name, values.ToolkitId, values.Version, true);
    }
}
