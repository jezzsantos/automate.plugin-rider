package jezzsantos.automate.plugin.application.interfaces.drafts;

import com.google.gson.Gson;
import jezzsantos.automate.core.AutomateConstants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DraftLiteTests {

    @Test
    public void whenDeserialized_ThenPopulates() {

        var json = "{" +
          "        \"DraftId\": \"anid\"," +
          "        \"DraftName\": \"aname\"," +
          "        \"ToolkitId\": \"atoolkitid\"," +
          "        \"ToolkitVersion\": {" +
          "             \"DraftCompatibility\": \"DraftAheadOfToolkit\"," +
          "             \"Toolkit\": {" +
          "                 \"Created\": \"1.0.0\"," +
          "                 \"Installed\": \"1.0.0\"" +
          "             }," +
          "             \"Runtime\": {" +
          "                 \"Created\": \"2.0.0\"," +
          "                 \"Installed\": \"2.0.0\"" +
          "             }," +
          "             \"Compatibility\": \"ToolkitAheadOfRuntime\"" +
          "}," +
          "        \"IsCurrent\": true" +
          "}";
        var gson = new Gson();

        var result = gson.fromJson(json, DraftLite.class);

        assertTrue(result.isIncompatible());

        assertEquals("anid", result.getId());
        assertEquals("aname", result.getName());
        assertEquals(AutomateConstants.DraftCompatibility.DRAFT_AHEADOF_TOOLKIT, result.getVersion().getDraftCompatibility());
        assertEquals("1.0.0", result.getVersion().getToolkitVersion().getCreated());
        assertEquals("2.0.0", result.getVersion().getRuntimeVersion().getCreated());
        assertEquals(AutomateConstants.ToolkitCompatibility.TOOLKIT_AHEADOF_RUNTIME, result.getVersion().getToolkitCompatibility());
        assertTrue(result.getIsCurrent());
    }

    @Test
    public void whenCreateIncompatibleWithIncompatibleDraft_ThenReturnsAnIncompatibleDraft() {

        var result = new DraftLite("anid", "aname", "atoolkitid", "1.0.0", "2.0.0", AutomateConstants.DraftCompatibility.DRAFT_AHEADOF_TOOLKIT, true);

        assertTrue(result.isIncompatible());
    }

    @Test
    public void whenCreateIncompatibleWithIncompatibleToolkit_ThenReturnsAnIncompatibleDraft() {

        var result = new DraftLite("anid", "aname", "atoolkitid", "1.0.0", "2.0.0", AutomateConstants.ToolkitCompatibility.RUNTIME_AHEADOF_TOOLKIT, true);

        assertTrue(result.isIncompatible());
    }

    @Test
    public void whenToString_ThenReturnsString() {

        var draft = new DraftLite("anid", "aname", "atoolkitid", "1.0.0", "2.0.0", true);

        var result = draft.toString();

        assertEquals("aname  (v1.0.0)", result);
    }
}
