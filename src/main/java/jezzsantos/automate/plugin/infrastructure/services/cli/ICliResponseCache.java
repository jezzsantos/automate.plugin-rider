package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftDetailed;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternDetailed;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitDetailed;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

public interface ICliResponseCache {

    @NotNull AllStateLite listAll(@NotNull Supplier<AllStateLite> supplier, boolean forceRefresh);

    @NotNull List<PatternLite> listPatterns(@NotNull Supplier<List<PatternLite>> supplier);

    @NotNull List<ToolkitLite> listToolkits(@NotNull Supplier<List<ToolkitLite>> supplier);

    @NotNull List<DraftLite> listDrafts(@NotNull Supplier<List<DraftLite>> supplier);

    @Nullable PatternLite getPatternInfo(@NotNull Supplier<PatternLite> supplier);

    @NotNull PatternDetailed getPatternDetailed(@NotNull Callable<PatternDetailed> supplier) throws Exception;

    @NotNull ToolkitDetailed getToolkitDetailed(@NotNull Callable<ToolkitDetailed> supplier) throws Exception;

    @Nullable DraftLite getDraftInfo(@NotNull Supplier<DraftLite> supplier);

    @NotNull DraftDetailed getDraftDetailed(@NotNull Callable<DraftDetailed> supplier) throws Exception;

    boolean isCliInstalled(@NotNull Supplier<Boolean> supplier);

    void invalidateAllLocalState();

    void invalidateAllPatterns();

    void invalidateAllToolkits();

    void invalidateAllDrafts();

    void invalidateCurrentPattern();

    void invalidateCurrentToolkit();

    void invalidateCurrentDraft();

    void invalidateIsCliInstalled();

    void setIsCliInstalled(boolean isInstalled);
}
