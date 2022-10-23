package jezzsantos.automate.plugin.infrastructure.services.cli.responses;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.drafts.LaunchPointExecutionResult;

import java.util.ArrayList;
import java.util.List;

class LaunchPointResult {

    public String Message;
    public AutomateConstants.CommandExecutionLogItemType Type;
}

class ValidationContext {

    public String Path;
}

class ValidationResult {

    public String Message;
    public ValidationContext Context;
}

@SuppressWarnings("unused")
class LaunchPointResults {

    public String Command;
    public List<LaunchPointResult> Log;
    public List<ValidationResult> ValidationErrors;
}

public class ExecuteLaunchPointStructuredOutput extends StructuredOutput<LaunchPointResults> {

    @UsedImplicitly
    public ExecuteLaunchPointStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new LaunchPointResults();
        }})));
    }

    public LaunchPointExecutionResult getResult() {

        var values = this.Output.get(0).Values;
        var result = new LaunchPointExecutionResult();

        if (values.Log != null) {
            values.Log.forEach(entry -> result.addLog(entry.Type, entry.Message));
        }
        if (values.ValidationErrors != null) {
            values.ValidationErrors.forEach(validation -> result.addValidation(validation.Context.Path, validation.Message));
        }

        return result;
    }
}
