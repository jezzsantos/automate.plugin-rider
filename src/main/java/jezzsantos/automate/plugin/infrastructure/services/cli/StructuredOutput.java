package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.jetbrains.rd.util.UsedImplicitly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

final class StructuredOutputError {

    public String Message;

    @UsedImplicitly
    public StructuredOutputError() {}

    @TestOnly
    public StructuredOutputError(@NotNull String message) {

        this.Message = message;
    }
}

@UsedImplicitly
class StructuredOutputOutput<TValues> {

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

