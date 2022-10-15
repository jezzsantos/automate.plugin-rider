package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftDetailed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class GetDraftStructuredOutput extends StructuredOutput<DraftDetailed> {

    @UsedImplicitly
    public GetDraftStructuredOutput() {

    }

    @TestOnly
    public GetDraftStructuredOutput(@NotNull String id, @NotNull String name, @NotNull String toolkitId, @NotNull String toolkitName, @NotNull String toolkitVersion, @NotNull String runtimeVersion, @NotNull HashMap<String, Object> configuration) {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new DraftDetailed(id, name, toolkitId, toolkitName, toolkitVersion, runtimeVersion, configuration);
        }})));
    }

    public DraftDetailed getDraft() {

        return this.Output.get(0).Values;
    }
}
