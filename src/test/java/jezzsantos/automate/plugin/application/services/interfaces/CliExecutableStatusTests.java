package jezzsantos.automate.plugin.application.services.interfaces;

import jezzsantos.automate.core.AutomateConstants;
import org.junit.jupiter.api.Test;

import java.lang.module.ModuleDescriptor.Version;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CliExecutableStatusTests {

    @Test
    public void whenConstructedWithoutVersion_ThenIsUnknown() {

        var status = new CliExecutableStatus("anexecutablename");

        assertEquals(CliVersionCompatibility.UNKNOWN, status.getCompatibility());
        assertEquals("", status.getVersion());
        assertEquals(AutomateConstants.MinimumSupportedVersion, status.getMinCompatibleVersion());
        assertEquals("anexecutablename", status.getExecutableName());
    }

    @Test
    public void whenConstructedWithInvalidSemanticVersion_ThenIsUnknown() {

        var status = new CliExecutableStatus("anexecutablename", "notavalidversion");

        var result = status.getCompatibility();

        assertEquals(CliVersionCompatibility.UNKNOWN, result);
        assertEquals("", status.getVersion());
        assertEquals(AutomateConstants.MinimumSupportedVersion, status.getMinCompatibleVersion());
        assertEquals("anexecutablename", status.getExecutableName());
    }

    @Test
    public void whenConstructedWithOlderSemanticVersion_ThenIsUnsupported() {

        var olderVersion = Version.parse("0.0.1-preview");

        var status = new CliExecutableStatus("anexecutablename", olderVersion.toString());

        var result = status.getCompatibility();

        assertEquals(CliVersionCompatibility.INCOMPATIBLE, result);
        assertEquals("0.0.1-preview", status.getVersion());
        assertEquals(AutomateConstants.MinimumSupportedVersion, status.getMinCompatibleVersion());
        assertEquals("anexecutablename", status.getExecutableName());
    }

    @Test
    public void whenConstructedWithSameSemanticVersion_ThenIsSupported() {

        var sameVersion = Version.parse(AutomateConstants.MinimumSupportedVersion);

        var status = new CliExecutableStatus("anexecutablename", sameVersion.toString());

        var result = status.getCompatibility();

        assertEquals(CliVersionCompatibility.COMPATIBLE, result);
        assertEquals(AutomateConstants.MinimumSupportedVersion, status.getVersion());
        assertEquals(AutomateConstants.MinimumSupportedVersion, status.getMinCompatibleVersion());
        assertEquals("anexecutablename", status.getExecutableName());
    }

    @Test
    public void whenConstructedWithNewerSemanticVersion_ThenIsSupported() {

        var newerVersion = Version.parse("100.0.0");

        var status = new CliExecutableStatus("anexecutablename", newerVersion.toString());

        var result = status.getCompatibility();

        assertEquals(CliVersionCompatibility.COMPATIBLE, result);
        assertEquals("100.0.0", status.getVersion());
        assertEquals(AutomateConstants.MinimumSupportedVersion, status.getMinCompatibleVersion());
        assertEquals("anexecutablename", status.getExecutableName());
    }
}
