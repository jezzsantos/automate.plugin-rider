package jezzsantos.automate.plugin.infrastructure.services.cli.responses;

import com.jetbrains.rd.util.UsedImplicitly;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
class BuildToolkit {

    public String Name;
    public String Version;
    public String FilePath;
    public String Warning;
}

public class PublishToolkitStructuredOutput extends StructuredOutput<BuildToolkit> {

    @UsedImplicitly
    public PublishToolkitStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new BuildToolkit();
        }})));
    }

    @Nullable
    public String getWarning() {

        if (this.Output.size() < 2) {
            return null;
        }

        return this.Output.get(1).Values.Warning;
    }
}