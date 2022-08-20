package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.AllDefinitions;
import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;
import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;
import jezzsantos.automate.plugin.application.interfaces.ToolkitDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class InMemAutomationCache implements IAutomationCache {

    @Nullable
    private List<PatternDefinition> patternsList;
    @Nullable
    private List<ToolkitDefinition> toolkitsList;
    @Nullable
    private List<DraftDefinition> draftsList;

    public void setAllLists(@NotNull AllDefinitions all) {
        this.patternsList = all.getPatterns();
        this.toolkitsList = all.getToolkits();
        this.draftsList = all.getDrafts();
    }

    @NotNull
    @Override
    public AllDefinitions ListAll(@NotNull Supplier<AllDefinitions> supplier, boolean forceRefresh) {
        if (forceRefresh) {
            invalidateAllLists();
        }

        if (this.patternsList == null || this.toolkitsList == null || this.draftsList == null) {
            var all = supplier.get();
            this.patternsList = all.getPatterns();
            this.toolkitsList = all.getToolkits();
            this.draftsList = all.getDrafts();
            return all;
        }

        return new AllDefinitions(this.patternsList, this.toolkitsList, this.draftsList);
    }

    @NotNull
    @Override
    public List<PatternDefinition> ListPatterns(@NotNull Supplier<List<PatternDefinition>> supplier) {
        if (this.patternsList == null) {
            this.patternsList = supplier.get();
        }

        return this.patternsList;
    }

    @NotNull
    @Override
    public List<ToolkitDefinition> ListToolkits(@NotNull Supplier<List<ToolkitDefinition>> supplier) {
        if (this.toolkitsList == null) {
            this.toolkitsList = supplier.get();
        }

        return this.toolkitsList;
    }

    @NotNull
    @Override
    public List<DraftDefinition> ListDrafts(@NotNull Supplier<List<DraftDefinition>> supplier) {
        if (this.draftsList == null) {
            this.draftsList = supplier.get();
        }

        return this.draftsList;
    }

    @Override
    public void invalidateAllLists() {
        this.patternsList = null;
        this.toolkitsList = null;
        this.draftsList = null;
    }

    @Override
    public void invalidatePatternList() {
        this.patternsList = null;
    }

    @Override
    public void invalidateToolkitList() {
        this.toolkitsList = null;
    }

    @Override
    public void invalidateDraftList() {
        this.draftsList = null;
    }

    @Nullable
    @Override
    public PatternDefinition GetPattern(@NotNull Supplier<PatternDefinition> supplier) {
        return supplier.get();
    }

    @Nullable
    @Override
    public DraftDefinition GetDraft(@NotNull Supplier<DraftDefinition> supplier) {
        return supplier.get();
    }
}
