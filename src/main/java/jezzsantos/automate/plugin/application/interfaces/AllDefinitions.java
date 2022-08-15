package jezzsantos.automate.plugin.application.interfaces;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AllDefinitions {

    @NotNull
    private final List<PatternDefinition> patterns;
    @NotNull
    private final List<ToolkitDefinition> toolkits;
    @NotNull
    private final List<DraftDefinition> drafts;

    public AllDefinitions() {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public AllDefinitions(@NotNull List<PatternDefinition> patterns, @NotNull List<ToolkitDefinition> toolkits, @NotNull List<DraftDefinition> drafts) {
        this.patterns = patterns;
        this.toolkits = toolkits;
        this.drafts = drafts;
    }

    @NotNull
    public List<PatternDefinition> getPatterns() {
        return this.patterns;
    }

    @NotNull
    public List<ToolkitDefinition> getToolkits() {
        return this.toolkits;
    }

    @NotNull
    public List<DraftDefinition> getDrafts() {
        return this.drafts;
    }
}
