package jezzsantos.automate.plugin.infrastructure.common;

import jezzsantos.automate.plugin.application.interfaces.AllDefinitions;
import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;
import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;
import jezzsantos.automate.plugin.application.interfaces.ToolkitDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

interface IAutomationCache {
    @NotNull
    AllDefinitions ListAll(@NotNull Supplier<AllDefinitions> supplier);

    @NotNull
    List<PatternDefinition> ListPatterns(@NotNull Supplier<List<PatternDefinition>> supplier);

    @NotNull
    List<ToolkitDefinition> ListToolkits(@NotNull Supplier<List<ToolkitDefinition>> supplier);

    @NotNull
    List<DraftDefinition> ListDrafts(@NotNull Supplier<List<DraftDefinition>> supplier);

    void invalidateAllLists();

    void invalidatePatternList();

    void invalidateToolkitList();

    void invalidateDraftList();

    @Nullable
    PatternDefinition GetPattern(@NotNull Supplier<PatternDefinition> supplier);

    @Nullable
    DraftDefinition GetDraft(@NotNull Supplier<DraftDefinition> supplier);
}
