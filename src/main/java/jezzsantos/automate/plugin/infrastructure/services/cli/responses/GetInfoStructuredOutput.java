package jezzsantos.automate.plugin.infrastructure.services.cli.responses;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.interfaces.CliCollectUsage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
class AutomateInfo {

    public String Command;
    public String RuntimeVersion;
    public String Location;
    public CliCollectUsage CollectUsage;

    @TestOnly
    public AutomateInfo(@Nullable String version) {

        this.RuntimeVersion = version;
    }
}

public class GetInfoStructuredOutput extends StructuredOutput<AutomateInfo> {

    @UsedImplicitly
    public GetInfoStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new AutomateInfo(null);
        }})));
    }

    @TestOnly
    public GetInfoStructuredOutput(@NotNull String version) {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new AutomateInfo(version);
        }})));
    }

    public String getVersion() {

        return this.Output.get(0).Values.RuntimeVersion;
    }
}
