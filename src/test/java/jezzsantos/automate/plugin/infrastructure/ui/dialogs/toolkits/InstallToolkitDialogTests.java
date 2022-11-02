package jezzsantos.automate.plugin.infrastructure.ui.dialogs.toolkits;

import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class InstallToolkitDialogTests {

    @BeforeEach
    public void setUp() {

    }

    @Test
    public void whenDoValidateAndLocationEmpty_ThenReturnsError() {

        var result = InstallToolkitDialog.doValidate(new InstallToolkitDialog.InstallToolkitDialogContext(), "");

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.InstallToolkit.LocationValidation.None.Message", AutomateConstants.ToolkitFileExtension), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndLocationIsNotAFile_ThenReturnsError() {

        var result = InstallToolkitDialog.doValidate(new InstallToolkitDialog.InstallToolkitDialogContext(), "notanexistingfile");

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.InstallToolkit.LocationValidation.None.Message", AutomateConstants.ToolkitFileExtension), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidate_ThenReturnsNull() throws IOException {

        var file = File.createTempFile("aprefix", "asuffix");
        try {
            var result = InstallToolkitDialog.doValidate(new InstallToolkitDialog.InstallToolkitDialogContext(), file.getPath());

            assertNull(result);
        } finally {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
    }
}
