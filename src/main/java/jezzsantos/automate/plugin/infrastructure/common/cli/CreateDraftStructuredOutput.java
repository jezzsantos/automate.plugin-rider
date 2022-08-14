package jezzsantos.automate.plugin.infrastructure.common.cli;


import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;

class CreateDraft {
    public String Name;
    public String DraftId;

    public String PatternName;
    public String Version;
}

public class CreateDraftStructuredOutput extends StructuredOutput<CreateDraft> {
    public DraftDefinition getDraft() {
        var values = this.Output.get(0).Values;
        return new DraftDefinition(values.DraftId, values.Name, values.Version, true);
    }
}
