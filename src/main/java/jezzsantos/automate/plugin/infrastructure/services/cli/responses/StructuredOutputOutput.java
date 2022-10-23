package jezzsantos.automate.plugin.infrastructure.services.cli.responses;

import com.jetbrains.rd.util.UsedImplicitly;
import org.jetbrains.annotations.TestOnly;

@UsedImplicitly
public class StructuredOutputOutput<TValues> {

    public String Message;
    public TValues Values;

    @UsedImplicitly
    public StructuredOutputOutput() {

    }

    @TestOnly
    public StructuredOutputOutput(TValues values) {

        this.Values = values;
    }
}
