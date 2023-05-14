package jezzsantos.automate.plugin.infrastructure.services.cli.responses.patterns;

import jezzsantos.automate.plugin.application.interfaces.patterns.CodeTemplate;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutput;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutputOutput;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
class AddRemoveCodeTemplate {

    public String Name;
    public String TemplateId;
    public String ParentId;
    public String OriginalFilePath;
    public String OriginalFileExtension;
    public String EditorPath;
}

public class AddRemovePatternCodeTemplateStructuredOutput extends StructuredOutput<AddRemoveCodeTemplate> {

    @TestOnly
    public AddRemovePatternCodeTemplateStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
                                          this.Values = new AddRemoveCodeTemplate();
                                      }}
        )));
    }

    public CodeTemplate getCodeTemplate() {

        var values = this.Output.get(0).Values;
        return new CodeTemplate(values.TemplateId, values.Name, values.OriginalFilePath, values.OriginalFileExtension, values.EditorPath);
    }
}

