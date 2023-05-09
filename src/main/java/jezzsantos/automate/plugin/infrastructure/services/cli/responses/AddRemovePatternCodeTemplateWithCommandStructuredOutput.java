package jezzsantos.automate.plugin.infrastructure.services.cli.responses;

import jezzsantos.automate.plugin.application.interfaces.patterns.Automation;
import jezzsantos.automate.plugin.application.interfaces.patterns.CodeTemplate;
import jezzsantos.automate.plugin.application.interfaces.patterns.CodeTemplateWithCommand;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

class AddRemoveCodeTemplateWithCommand {

    public AddRemoveCodeTemplate CodeTemplate = new AddRemoveCodeTemplate();
    public AddRemoveAutomation Command = new AddRemoveAutomation();
}

class AddRemoveAutomation {

    public String Name;
    public String Id;
    public String ParentId;
    public String Type;
    public CodeTemplateCommandMetadata Metadata;
}

class CodeTemplateCommandMetadata {

    public String CodeTemplateId;
    public boolean IsOneOff;
    public String FilePath;
}

public class AddRemovePatternCodeTemplateWithCommandStructuredOutput extends StructuredOutput<AddRemoveCodeTemplateWithCommand> {

    @TestOnly
    public AddRemovePatternCodeTemplateWithCommandStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
                                          this.Values = new AddRemoveCodeTemplateWithCommand();
                                      }}
        )));
    }

    public CodeTemplateWithCommand getCodeTemplateWithCommand() {

        var codeTemplateValues = this.Output.get(0).Values;
        var commandValues = this.Output.get(1).Values;
        return new CodeTemplateWithCommand(
          new CodeTemplate(codeTemplateValues.CodeTemplate.Id, codeTemplateValues.CodeTemplate.Name, codeTemplateValues.CodeTemplate.OriginalFilePath,
                           codeTemplateValues.CodeTemplate.OriginalFileExtension, codeTemplateValues.CodeTemplate.EditorPath),
          Automation.createCodeTemplateCommand(commandValues.Command.Id, commandValues.Command.Name, commandValues.Command.Metadata.CodeTemplateId,
                                               commandValues.Command.Metadata.IsOneOff, commandValues.Command.Metadata.FilePath));
    }
}
