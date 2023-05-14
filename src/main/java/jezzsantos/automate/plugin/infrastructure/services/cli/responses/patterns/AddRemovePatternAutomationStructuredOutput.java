package jezzsantos.automate.plugin.infrastructure.services.cli.responses.patterns;

import jezzsantos.automate.plugin.application.interfaces.patterns.Automation;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutput;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutputOutput;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
class AddRemoveAutomation {

    public String Name;
    public String CommandId;
    public String LaunchPointId;
    public String ParentId;
    public String Type;
    public AutomationMetadata Metadata;
}

class AutomationMetadata {

    public String CodeTemplateId;
    public String FilePath;
    public boolean IsOneOff;
    public String ApplicationName;
    public String Arguments;
    public String CommandIds;
}

public class AddRemovePatternAutomationStructuredOutput extends StructuredOutput<AddRemoveAutomation> {

    @TestOnly
    public AddRemovePatternAutomationStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
                                          this.Values = new AddRemoveAutomation();
                                      }}
        )));
    }

    public Automation getCodeTemplateCommand() {

        var values = this.Output.get(0).Values;
        return Automation.createCodeTemplateCommand(values.CommandId, values.Name, values.Metadata.CodeTemplateId,
                                                    values.Metadata.IsOneOff, values.Metadata.FilePath);
    }

    public Automation getCliCommand() {

        var values = this.Output.get(0).Values;
        return Automation.createCliCommand(values.CommandId, values.Name, values.Metadata.ApplicationName, values.Metadata.Arguments);
    }

    public Automation getLaunchPoint() {

        var values = this.Output.get(0).Values;
        return Automation.createLaunchPoint(values.LaunchPointId, values.Name, values.Metadata.CommandIds);
    }
}
