package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NewPatternDialogContext {

    public List<PatternLite> Patterns;
    public String Name;

    public NewPatternDialogContext(@NotNull List<PatternLite> patterns) {
        this.Patterns = patterns;
    }
}
