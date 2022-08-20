package jezzsantos.automate.plugin.instrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.AllDefinitions;
import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;
import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;
import jezzsantos.automate.plugin.application.interfaces.ToolkitDefinition;
import jezzsantos.automate.plugin.infrastructure.services.cli.InMemAutomationCache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemAutomationCacheTests {

    private InMemAutomationCache cache;

    @BeforeEach
    public void setUp() {
        this.cache = new InMemAutomationCache();
    }

    @Test
    public void whenListAllAndNotCached_ThenReturnsSupplied() {
        var allLists = new AllDefinitions();

        var result = this.cache.ListAll(() -> allLists, false);

        assertEquals(allLists, result);
    }

    @Test
    public void whenListAllAndNotCachedAndForced_ThenReturnsSupplied() {
        var allLists = new AllDefinitions();

        var result = this.cache.ListAll(() -> allLists, true);

        assertEquals(allLists, result);
    }

    @Test
    public void whenListAllAndCachedAndNotForced_ThenFetchesFromCache() {
        var patterns = new ArrayList<PatternDefinition>();
        var toolkits = new ArrayList<ToolkitDefinition>();
        var drafts = new ArrayList<DraftDefinition>();
        var allLists = new AllDefinitions(patterns, toolkits, drafts);
        this.cache.setAllLists(allLists);

        var result = this.cache.ListAll(Assertions::fail, false);

        assertEquals(patterns, result.getPatterns());
        assertEquals(toolkits, result.getToolkits());
        assertEquals(drafts, result.getDrafts());
    }

    @Test
    public void whenListAllAndCachedAndForced_ThenReturnsSupplied() {
        var patterns = new ArrayList<PatternDefinition>();
        var toolkits = new ArrayList<ToolkitDefinition>();
        var drafts = new ArrayList<DraftDefinition>();
        var allLists = new AllDefinitions(patterns, toolkits, drafts);
        this.cache.setAllLists(allLists);

        var result = this.cache.ListAll(() -> allLists, true);

        assertEquals(allLists, result);
    }

    @Test
    public void whenListAllAndCachedAndNotForcedButInvalidated_ThenReturnsSupplied() {
        var patterns = new ArrayList<PatternDefinition>();
        var toolkits = new ArrayList<ToolkitDefinition>();
        var drafts = new ArrayList<DraftDefinition>();
        var allLists = new AllDefinitions(patterns, toolkits, drafts);
        this.cache.setAllLists(allLists);
        this.cache.invalidateAllLists();

        var result = this.cache.ListAll(() -> allLists, false);

        assertEquals(allLists, result);
    }
}
