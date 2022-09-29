//package jezzsantos.automate.plugin.infrastructure.services.cli;
//
//import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
//import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
//import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
//import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class InMemAutomationCacheTests {
//
//    private InMemAutomationCache cache;
//
//    @BeforeEach
//    public void setUp() {
//
//        this.cache = new InMemAutomationCache();
//    }
//
//    @Test
//    public void whenListAllAndNotCached_ThenReturnsSupplied() {
//
//        var allLists = new AllStateLite();
//
//        var result = this.cache.ListAll(() -> allLists, false);
//
//        assertEquals(allLists, result);
//    }
//
//    @Test
//    public void whenListAllAndNotCachedAndForced_ThenReturnsSupplied() {
//
//        var allLists = new AllStateLite();
//
//        var result = this.cache.ListAll(() -> allLists, true);
//
//        assertEquals(allLists, result);
//    }
//
//    @Test
//    public void whenListAllAndCachedAndNotForced_ThenFetchesFromCache() {
//
//        var patterns = new ArrayList<PatternLite>();
//        var toolkits = new ArrayList<ToolkitLite>();
//        var drafts = new ArrayList<DraftLite>();
//        var allLists = new AllStateLite(patterns, toolkits, drafts);
//        this.cache.setAllLists(allLists);
//
//        var result = this.cache.ListAll(Assertions::fail, false);
//
//        assertEquals(patterns, result.getPatterns());
//        assertEquals(toolkits, result.getToolkits());
//        assertEquals(drafts, result.getDrafts());
//    }
//
//    @Test
//    public void whenListAllAndCachedAndForced_ThenReturnsSupplied() {
//
//        var patterns = new ArrayList<PatternLite>();
//        var toolkits = new ArrayList<ToolkitLite>();
//        var drafts = new ArrayList<DraftLite>();
//        var allLists = new AllStateLite(patterns, toolkits, drafts);
//        this.cache.setAllLists(allLists);
//
//        var result = this.cache.ListAll(() -> allLists, true);
//
//        assertEquals(allLists, result);
//    }
//
//    @Test
//    public void whenListAllAndCachedAndNotForcedButInvalidated_ThenReturnsSupplied() {
//
//        var patterns = new ArrayList<PatternLite>();
//        var toolkits = new ArrayList<ToolkitLite>();
//        var drafts = new ArrayList<DraftLite>();
//        var allLists = new AllStateLite(patterns, toolkits, drafts);
//        this.cache.setAllLists(allLists);
//        this.cache.invalidateAllLocalState();
//
//        var result = this.cache.ListAll(() -> allLists, false);
//
//        assertEquals(allLists, result);
//    }
//}
