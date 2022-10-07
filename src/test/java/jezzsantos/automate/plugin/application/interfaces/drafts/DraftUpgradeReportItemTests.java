package jezzsantos.automate.plugin.application.interfaces.drafts;

import jezzsantos.automate.core.AutomateConstants;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DraftUpgradeReportItemTests {

    @Test
    public void whenGetMessageWithNoPlaceholders_ThenReturnsFormattedMessage() {

        var result = new DraftUpgradeReportItem(AutomateConstants.UpgradeLogType.ABORT, "amessagetemplate", Map.of());

        assertEquals("amessagetemplate", result.getMessage());
    }

    @Test
    public void whenGetMessageWithSomePlaceholders_ThenReturnsFormattedMessage() {

        var result = new DraftUpgradeReportItem(AutomateConstants.UpgradeLogType.ABORT, "{placeholder1}", Map.of("placeholder1", "avalue"));

        assertEquals("avalue", result.getMessage());
    }
}
