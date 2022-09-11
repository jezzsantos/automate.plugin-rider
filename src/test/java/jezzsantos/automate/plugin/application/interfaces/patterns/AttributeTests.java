package jezzsantos.automate.plugin.application.interfaces.patterns;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AttributeTests {

    @Test
    public void whenToStringWithNoDefaultValueAndNotRequired_ThenReturnsString() {

        var result = new Attribute("anid", "aname")
          .toString();

        assertEquals("aname  (string, optional)", result);
    }

    @Test
    public void whenToStringWithNoDefaultValueAndRequired_ThenReturnsString() {

        var result = new Attribute("anid", "aname", true, null, null, null)
          .toString();

        assertEquals("aname  (string, required)", result);
    }

    @Test
    public void whenToStringWithDefaultValue_ThenReturnsString() {

        var result = new Attribute("anid", "aname", false, "adefaultvalue", null, null)
          .toString();

        assertEquals("aname  (string, optional, default: adefaultvalue)", result);
    }

    @Test
    public void whenToStringWithChoicesAndNoDefaultValue_ThenReturnsString() {

        var result = new Attribute("anid", "aname", false, null, null, List.of("achoice1", "achoice2", "achoice3"))
          .toString();

        assertEquals("aname  (string, optional, oneof: achoice1;achoice2;achoice3)", result);
    }

    @Test
    public void whenToStringWithChoicesAndDefaultValue_ThenReturnsString() {

        var result = new Attribute("anid", "aname", false, "adefaultvalue", null, List.of("achoice1", "achoice2", "achoice3"))
          .toString();

        assertEquals("aname  (string, optional, oneof: achoice1;achoice2;achoice3, default: adefaultvalue)", result);
    }
}
