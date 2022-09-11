package jezzsantos.automate.plugin.application.interfaces.toolkits;

import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ToolkitDetailedTests {

    @Test
    public void whenGetPattern_ThenReturnsPattern() {

        var pattern = new PatternElement("anid", "aname");

        var result = new ToolkitDetailed("anid", "aname", "aversion", pattern)
          .getPattern();

        assertEquals(pattern, result);
        assertTrue(result.isRoot());
    }

    @Test
    public void whenToString_ThenReturnsString() {

        var pattern = new PatternElement("anid", "aname");

        var result = new ToolkitDetailed("anid", "aname", "aversion", pattern)
          .toString();

        assertEquals("aname  (anid)", result);
    }
}
