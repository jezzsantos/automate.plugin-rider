package jezzsantos.automate.plugin.application.interfaces;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AllStateLite {

    @NotNull
    private final List<PatternLite> patterns;
    @NotNull
    private final List<ToolkitLite> toolkits;
    @NotNull
    private final List<DraftLite> drafts;

    public AllStateLite() {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public AllStateLite(@NotNull List<PatternLite> patterns, @NotNull List<ToolkitLite> toolkits, @NotNull List<DraftLite> drafts) {
        this.patterns = patterns;
        this.toolkits = toolkits;
        this.drafts = drafts;
    }

    @NotNull
    public List<PatternLite> getPatterns() {
        return this.patterns;
    }

    @NotNull
    public List<ToolkitLite> getToolkits() {
        return this.toolkits;
    }

    @NotNull
    public List<DraftLite> getDrafts() {
        return this.drafts;
    }
}
