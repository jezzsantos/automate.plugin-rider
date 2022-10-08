package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.jetbrains.rd.util.UsedImplicitly;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
class BuildToolkit {

    public String Name;
    public String Version;
    public String FilePath;
}

public class PublishToolkitStructuredOutput extends StructuredOutput<BuildToolkit> {

    @UsedImplicitly
    public PublishToolkitStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new BuildToolkit();
        }})));
    }
}