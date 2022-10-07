package jezzsantos.automate.plugin.application.interfaces.patterns;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatternDetailedTests {

    @Test
    public void whenGetPattern_ThenReturnsRoot() {

        var pattern = new PatternElement("apatternid", "apatternname");

        var result = new PatternDetailed("anid", "aname", new PatternVersion("aversion"), pattern)
          .getPattern();

        assertEquals(pattern, result);
        assertTrue(result.isRoot());
    }

    @Test
    public void whenToString_ThenReturnsString() {

        var pattern = new PatternElement("apatternid", "apatternname");

        var result = new PatternDetailed("anid", "aname", new PatternVersion("aversion"), pattern)
          .toString();

        assertEquals("aname  (vaversion)", result);
    }
}
