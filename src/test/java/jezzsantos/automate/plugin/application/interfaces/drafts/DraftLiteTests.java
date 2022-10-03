package jezzsantos.automate.plugin.application.interfaces.drafts;

import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DraftLiteTests {

    @Test
    public void whenDeserialized_ThenPopulates() {

        var json = "{" +
          "        \"Id\": \"anid\"," +
          "        \"Name\": \"aname\"," +
          "        \"ToolkitId\": \"atoolkitid\"," +
          "        \"ToolkitVersion\": \"1.0.0\"," +
          "        \"CurrentToolkitVersion\": \"2.0.0\"," +
          "        \"IsCurrent\": true" +
          "}";
        var gson = new Gson();

        var result = gson.fromJson(json, DraftLite.class);

        assertTrue(result.mustBeUpgraded());

        assertEquals("anid", result.getId());
        assertEquals("aname", result.getName());
        assertEquals("1.0.0", result.getOriginalToolkitVersion());
        assertEquals("2.0.0", result.getCurrentToolkitVersion());
        assertTrue(result.getIsCurrent());
    }

    @Test
    public void whenConstructedWithLaterMinorVersion_ThenMustNotBeUpgraded() {

        var result = new DraftLite("anid", "aname", "atoolkitid", "1.0.0", "1.1.0", true);

        assertFalse(result.mustBeUpgraded());
    }

    @Test
    public void whenConstructedWithLaterMajorVersion_ThenMustBeUpgraded() {

        var result = new DraftLite("anid", "aname", "atoolkitid", "1.0.0", "2.0.0", true);

        assertTrue(result.mustBeUpgraded());
    }

    @Test
    public void whenToString_ThenReturnsString() {

        var draft = new DraftLite("anid", "aname", "atoolkitid", "1.0.0", true);

        var result = draft.toString();

        assertEquals("aname  (v1.0.0)", result);
    }
}
