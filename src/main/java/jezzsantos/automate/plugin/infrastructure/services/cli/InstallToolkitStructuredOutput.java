package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.jetbrains.rd.util.UsedImplicitly;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
class InstallToolkit {

    public String Name;
    public String Version;
}

public class InstallToolkitStructuredOutput extends StructuredOutput<InstallToolkit> {

    @UsedImplicitly
    public InstallToolkitStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new InstallToolkit();
        }})));
    }
}
