package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.jetbrains.rd.util.UsedImplicitly;

import java.util.ArrayList;
import java.util.List;

final class StructuredOutputError {

    public String Message;
}

@UsedImplicitly
class StructuredOutputOutput<TValues> {

    public String Message;
    public TValues Values;
}

@SuppressWarnings("unused")
public abstract class StructuredOutput<TValues> {

    public List<String> Info = new ArrayList<>();
    public List<StructuredOutputOutput<TValues>> Output = new ArrayList<>();
    public StructuredOutputError Error = null;

    @UsedImplicitly
    public StructuredOutput() {

    }

    protected StructuredOutput(List<StructuredOutputOutput<TValues>> outputs) {

        this.Output = outputs;
    }
}

