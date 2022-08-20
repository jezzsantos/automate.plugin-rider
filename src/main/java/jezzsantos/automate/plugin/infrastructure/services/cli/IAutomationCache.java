package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftDetailed;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternDetailed;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public interface IAutomationCache {
    @NotNull AllStateLite ListAll(@NotNull Supplier<AllStateLite> supplier, boolean forceRefresh);

    @NotNull List<PatternLite> ListPatterns(@NotNull Supplier<List<PatternLite>> supplier);

    @NotNull List<ToolkitLite> ListToolkits(@NotNull Supplier<List<ToolkitLite>> supplier);

    @NotNull List<DraftLite> ListDrafts(@NotNull Supplier<List<DraftLite>> supplier);

    void invalidateAllLocalState();

    void invalidateAllPatterns();

    void invalidateAllToolkits();

    void invalidateAllDrafts();

    @Nullable PatternLite GetPatternInfo(@NotNull Supplier<PatternLite> supplier);

    @NotNull PatternDetailed GetPatternDetailed(@NotNull Callable<PatternDetailed> supplier) throws Exception;

    @Nullable DraftLite GetDraftInfo(@NotNull Supplier<DraftLite> supplier);

    @NotNull DraftDetailed GetDraftDetailed(@NotNull Callable<DraftDetailed> supplier) throws Exception;

}
