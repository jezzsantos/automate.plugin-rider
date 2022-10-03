package jezzsantos.automate.plugin.application.interfaces.drafts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DraftUpgradeInfoTests {

    @Test
    public void whenConstructedWithSameVersions_ThenCompatible() {

        var result = new DraftUpgradeInfo("1.0.0", "1.0.0");

        assertTrue(result.isCompatible());
        assertEquals("1.0.0", result.getFromVersion());
        assertEquals("1.0.0", result.getToVersion());
        assertEquals(DraftCompatibility.COMPATIBLE, result.getCompatibility());
    }

    @Test
    public void whenConstructedWithHigherMinorVersion_ThenCompatible() {

        var result = new DraftUpgradeInfo("1.0.0", "1.1.0");

        assertTrue(result.isCompatible());
        assertEquals("1.0.0", result.getFromVersion());
        assertEquals("1.1.0", result.getToVersion());
        assertEquals(DraftCompatibility.COMPATIBLE, result.getCompatibility());
    }

    @Test
    public void whenConstructedWithHigherMajorVersion_ThenInCompatible() {

        var result = new DraftUpgradeInfo("1.0.0", "2.0.0");

        assertFalse(result.isCompatible());
        assertEquals("1.0.0", result.getFromVersion());
        assertEquals("2.0.0", result.getToVersion());
        assertEquals(DraftCompatibility.INCOMPATIBLE_TOOLKIT, result.getCompatibility());
    }
}
