package jezzsantos.automate.plugin.application.interfaces.patterns;

import org.jetbrains.annotations.NotNull;

public class CodeTemplateWithCommand {

    private final CodeTemplate codeTemplate;
    private final Automation automation;

    public CodeTemplateWithCommand(@NotNull CodeTemplate codeTemplate, @NotNull Automation automation) {

        this.codeTemplate = codeTemplate;
        this.automation = automation;
    }

    public CodeTemplate getCodeTemplate() {return this.codeTemplate;}

    public Automation getAutomation() {return this.automation;}
}
