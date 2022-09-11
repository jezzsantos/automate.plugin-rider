package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NewPatternDialogTests {

    @Test
    public void whenDoValidateAndEmptyName_ThenReturnsError() {

        var pattern = new PatternLite("anid", "apatternname", "aversion", false);
        var context = new NewPatternDialog.NewPatternDialogContext(new ArrayList<>(List.of(pattern)));

        var result = NewPatternDialog.doValidate(context, "");

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewPattern.NameValidation.NotMatch.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndInvalidName_ThenReturnsError() {

        var pattern = new PatternLite("anid", "apatternname", "aversion", false);
        var context = new NewPatternDialog.NewPatternDialogContext(new ArrayList<>(List.of(pattern)));

        var result = NewPatternDialog.doValidate(context, "^aninvalidname^");

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewPattern.NameValidation.NotMatch.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndNameIsReserved_ThenReturnsError() {

        var pattern = new PatternLite("anid", "apatternname", "aversion", false);
        var context = new NewPatternDialog.NewPatternDialogContext(new ArrayList<>(List.of(pattern)));

        var result = NewPatternDialog.doValidate(context, "apatternname");

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewPattern.NameValidation.Exists.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidate_ThenReturnsNull() {

        var pattern = new PatternLite("anid", "apatternname", "aversion", false);
        var context = new NewPatternDialog.NewPatternDialogContext(new ArrayList<>(List.of(pattern)));

        var result = NewPatternDialog.doValidate(context, "adraftname");

        assertNull(result);
    }

}
