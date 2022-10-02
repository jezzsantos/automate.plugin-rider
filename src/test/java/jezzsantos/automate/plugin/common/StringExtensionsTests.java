package jezzsantos.automate.plugin.common;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static jezzsantos.automate.plugin.common.StringExtensions.formatStructured;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StringExtensionsTests {

    @Test
    public void whenFormatStructuredWithEmptyTemplate_ThenReturnsEmpty() {

        var result = formatStructured("", Map.of());

        assertEquals("", result);
    }

    @Test
    public void whenFormatStructuredWithEmptyArgumentsAndNoPlaceholders_ThenReturnsTemplate() {

        var result = formatStructured("atemplate", Map.of());

        assertEquals("atemplate", result);
    }

    @Test
    public void whenFormatStructuredWithEmptyArgumentsAndHasOnlyPlaceholders_ThenReturnsMessage() {

        var result = formatStructured("{AName}", Map.of());

        assertEquals("{AName}", result);
    }

    @Test
    public void whenFormatStructuredWithEmptyArgumentsAndHasPlaceholders_ThenReturnsMessage() {

        var result = formatStructured("atemplate{AName}", Map.of());

        assertEquals("atemplate{AName}", result);
    }

    @Test
    public void whenFormatStructuredWithMorePlaceholdersThanArguments_ThenReturnsMessage() {

        var result = formatStructured("{AName1}{AName2}{AName3}", Map.of(
          "AName1", "avalue1",
          "AName3", 3
        ));

        assertEquals("avalue1{AName2}3", result);
    }

    @Test
    public void whenFormatStructuredWithMoreArgumentsThanPlaceholders_ThenReturnsMessage() {

        var result = formatStructured("{AName2}", Map.of(
          "AName1", "avalue1",
          "AName2", 2,
          "AName3", "avalue3"
        ));

        assertEquals("2", result);
    }

    @Test
    public void whenFormatStructuredWithEmptyArgument_ThenReturnsMessage() {

        var result = formatStructured("{AName1}", new HashMap<>() {{
                                          put("AName1", null);
                                      }}
        );

        assertEquals("{AName1}", result);
    }

    @Test
    public void whenFormatStructuredWithArgumentsThanDontMatchPlaceholders_ThenReturnsMessage() {

        var result = formatStructured("{AName2}", Map.of(
          "AnotherName1", "avalue1"
        ));

        assertEquals("{AName2}", result);
    }

    @Test
    public void whenFormatStructuredWithArgumentsThatMatchMultiplePlaceholders_ThenReturnsMessage() {

        var result = formatStructured("{AName1}{AName2}{AName3}{AName2}{AName1}", Map.of(
          "AName1", "avalue1",
          "AName2", "avalue2",
          "AName3", "avalue3"
        ));

        assertEquals("avalue1avalue2avalue3avalue2avalue1", result);
    }

    @Test
    public void whenFormatStructuredWithArgumentsThatMatchPlaceholders_ThenReturnsMessage() {

        var result = formatStructured("{AName1}{AName2}{AName3}", Map.of(
          "AName1", "avalue1",
          "AName2", "avalue2",
          "AName3", "avalue3"
        ));

        assertEquals("avalue1avalue2avalue3", result);
    }
}
