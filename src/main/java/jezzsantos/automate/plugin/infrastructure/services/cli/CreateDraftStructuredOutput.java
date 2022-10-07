package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
class CreateDraft {

    public String DraftName;
    public String DraftId;

    public String ToolkitName;
    public String ToolkitId;
    public String ToolkitVersion;
    public String RuntimeVersion;
}

public class CreateDraftStructuredOutput extends StructuredOutput<CreateDraft> {

    @UsedImplicitly
    public CreateDraftStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new CreateDraft();
        }})));
    }

    public DraftLite getDraft() {

        var values = this.Output.get(0).Values;
        return new DraftLite(values.DraftId, values.DraftName, values.ToolkitId, values.ToolkitVersion, values.RuntimeVersion, true);
    }
}
