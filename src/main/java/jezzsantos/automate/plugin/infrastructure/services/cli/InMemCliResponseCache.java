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

public class InMemCliResponseCache implements ICliResponseCache {

    @Nullable
    private List<PatternLite> patternsList;
    @Nullable
    private List<ToolkitLite> toolkitsList;
    @Nullable
    private List<DraftLite> draftsList;
    @Nullable
    private PatternDetailed currentPattern;
    @Nullable
    private DraftDetailed currentDraft;
    @Nullable
    private ToolkitDetailed currentToolkit;
    private Boolean isCliInstalled;

    public void setAllLists(@NotNull AllStateLite all) {

        this.patternsList = all.getPatterns();
        this.toolkitsList = all.getToolkits();
        this.draftsList = all.getDrafts();
    }

    @NotNull
    @Override
    public AllStateLite listAll(@NotNull Supplier<AllStateLite> supplier, boolean forceRefresh) {

        if (forceRefresh) {
            invalidateAllLocalState();
        }

        if (this.patternsList == null || this.toolkitsList == null || this.draftsList == null) {
            var all = supplier.get();
            this.patternsList = all.getPatterns();
            this.toolkitsList = all.getToolkits();
            this.draftsList = all.getDrafts();
            return all;
        }

        return new AllStateLite(this.patternsList, this.toolkitsList, this.draftsList);
    }

    @NotNull
    @Override
    public List<PatternLite> listPatterns(@NotNull Supplier<List<PatternLite>> supplier) {

        if (this.patternsList == null) {
            this.patternsList = supplier.get();
        }

        return this.patternsList;
    }

    @NotNull
    @Override
    public List<ToolkitLite> listToolkits(@NotNull Supplier<List<ToolkitLite>> supplier) {

        if (this.toolkitsList == null) {
            this.toolkitsList = supplier.get();
        }

        return this.toolkitsList;
    }

    @NotNull
    @Override
    public List<DraftLite> listDrafts(@NotNull Supplier<List<DraftLite>> supplier) {

        if (this.draftsList == null) {
            this.draftsList = supplier.get();
        }

        return this.draftsList;
    }

    @Nullable
    @Override
    public PatternLite getPatternInfo(@NotNull Supplier<PatternLite> supplier) {

        return supplier.get();
    }

    @NotNull
    @Override
    public PatternDetailed getPatternDetailed(@NotNull Callable<PatternDetailed> supplier) throws Exception {

        if (this.currentPattern == null) {
            this.currentPattern = supplier.call();
        }
        return this.currentPattern;
    }

    @Override
    public @NotNull ToolkitDetailed getToolkitDetailed(@NotNull Callable<ToolkitDetailed> supplier) throws Exception {

        if (this.currentToolkit == null) {
            this.currentToolkit = supplier.call();
        }
        return this.currentToolkit;
    }

    @Nullable
    @Override
    public DraftLite getDraftInfo(@NotNull Supplier<DraftLite> supplier) {

        return supplier.get();
    }

    @NotNull
    @Override
    public DraftDetailed getDraftDetailed(@NotNull Callable<DraftDetailed> supplier) throws Exception {

        if (this.currentDraft == null) {
            this.currentDraft = supplier.call();
        }
        return this.currentDraft;
    }

    @Override
    public boolean isCliInstalled(@NotNull Supplier<Boolean> supplier) {

        if (this.isCliInstalled == null) {
            this.isCliInstalled = supplier.get();
        }
        return this.isCliInstalled;
    }

    @Override
    public void invalidateAllLocalState() {

        invalidateAllPatterns();
        invalidateAllToolkits();
        invalidateAllDrafts();
    }

    @Override
    public void invalidateAllPatterns() {

        this.patternsList = null;
        invalidateCurrentPattern();
    }

    @Override
    public void invalidateAllToolkits() {

        this.toolkitsList = null;
        invalidateCurrentToolkit();
        invalidateAllDrafts();
    }

    @Override
    public void invalidateAllDrafts() {

        this.draftsList = null;
        invalidateCurrentDraft();
    }

    @Override
    public void invalidateCurrentPattern() {

        this.currentPattern = null;
    }

    @Override
    public void invalidateCurrentToolkit() {

        this.currentToolkit = null;
    }

    @Override
    public void invalidateCurrentDraft() {

        this.currentDraft = null;
    }

    @Override
    public void invalidateIsCliInstalled() {

        this.isCliInstalled = null;
    }

    @Override
    public void setIsCliInstalled(boolean isInstalled) {

        this.isCliInstalled = isInstalled;
    }

    @Override
    public void setDraftDetailed(@NotNull DraftDetailed draft) {

        this.currentDraft = draft;
    }
}
