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
          "        \"ToolkitName\": \"atoolkitname\"," +
          "        \"ToolkitVersion\": {" +
          "             \"DraftCompatibility\": \"DraftAheadOfToolkit\"," +
          "             \"Toolkit\": {" +
          "                 \"Published\": \"1.0.0\"," +
          "                 \"Installed\": \"1.0.0\"" +
          "             }," +
          "             \"Runtime\": {" +
          "                 \"Published\": \"2.0.0\"," +
          "                 \"Installed\": \"2.0.0\"" +
          "             }," +
          "             \"Compatibility\": \"ToolkitAheadOfMachine\"" +
          "}," +
          "        \"IsCurrent\": true" +
          "}";
        var gson = new Gson();

        var result = gson.fromJson(json, DraftLite.class);

        assertTrue(result.isIncompatible());

        assertEquals("anid", result.getId());
        assertEquals("aname", result.getName());
        assertEquals(AutomateConstants.DraftToolkitVersionCompatibility.DRAFT_AHEADOF_TOOLKIT, result.getVersion().getDraftCompatibility());
        assertEquals("1.0.0", result.getVersion().getToolkitVersion().getPublished());
        assertEquals("2.0.0", result.getVersion().getRuntimeVersion().getPublished());
        assertEquals(AutomateConstants.ToolkitRuntimeVersionCompatibility.TOOLKIT_AHEADOF_MACHINE, result.getVersion().getRuntimeCompatibility());
        assertTrue(result.getIsCurrent());
    }

    @Test
    public void whenCreateIncompatibleWithIncompatibleDraft_ThenReturnsAnIncompatibleDraft() {

        var result = new DraftLite("anid", "aname", "atoolkitid", "atoolkitname", "1.0.0", "2.0.0", AutomateConstants.DraftToolkitVersionCompatibility.DRAFT_AHEADOF_TOOLKIT, true);

        assertTrue(result.isIncompatible());
    }

    @Test
    public void whenCreateIncompatibleWithIncompatibleToolkit_ThenReturnsAnIncompatibleDraft() {

        var result = new DraftLite("anid", "aname", "atoolkitid", "atoolkitname", "1.0.0", "2.0.0", AutomateConstants.ToolkitRuntimeVersionCompatibility.MACHINE_AHEADOF_TOOLKIT,
                                   true);

        assertTrue(result.isIncompatible());
    }

    @Test
    public void whenToString_ThenReturnsString() {

        var draft = new DraftLite("anid", "aname", "atoolkitid", "atoolkitname", "1.0.0", "2.0.0", true);

        var result = draft.toString();

        assertEquals("aname  (v1.0.0)", result);
    }
}
