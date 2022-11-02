package jezzsantos.automate.plugin.infrastructure.ui.dialogs.patterns;

import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EditPatternElementDialogTests {

    @Test
    public void whenDoValidateAndNameIsEmpty_ThenReturnsError() {

        var context = new EditPatternElementDialog.EditPatternElementDialogContext(new ArrayList<>(), new ArrayList<>());

        var result = EditPatternElementDialog.doValidate(context, "", "adisplayname", "adescription");

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.EditPatternElement.NameValidation.NotMatch.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndNameIsInvalid_ThenReturnsError() {

        var context = new EditPatternElementDialog.EditPatternElementDialogContext(new ArrayList<>(), new ArrayList<>());

        var result = EditPatternElementDialog.doValidate(context, "^aninvalidname^", "adisplayname", "adescription");

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.EditPatternElement.NameValidation.NotMatch.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndNameIsReserved_ThenReturnsError() {

        var context = new EditPatternElementDialog.EditPatternElementDialogContext(new ArrayList<>(), new ArrayList<>(List.of(new PatternElement("anid", "anelementname"))));

        var result = EditPatternElementDialog.doValidate(context, "anelementname", "adisplayname", "adescription");

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.EditPatternElement.NameValidation.Exists.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndDisplayNameIsEmpty_ThenReturnsNull() {

        var context = new EditPatternElementDialog.EditPatternElementDialogContext(new ArrayList<>(), new ArrayList<>());

        var result = EditPatternElementDialog.doValidate(context, "anattributename", "", "adescription");

        assertNull(result);
    }

    @Test
    public void whenDoValidateAndDisplayNameIsAny_ThenReturnsNull() {

        var context = new EditPatternElementDialog.EditPatternElementDialogContext(new ArrayList<>(), new ArrayList<>());

        var result = EditPatternElementDialog.doValidate(context, "anattributename", "adisplayname", "adescription");

        assertNull(result);
    }

    @Test
    public void whenDoValidateAndDescriptionIsEmpty_ThenReturnsNull() {

        var context = new EditPatternElementDialog.EditPatternElementDialogContext(new ArrayList<>(), new ArrayList<>());

        var result = EditPatternElementDialog.doValidate(context, "anattributename", "adisplayname", "");

        assertNull(result);
    }

    @Test
    public void whenDoValidateAndDescriptionIsAny_ThenReturnsNull() {

        var context = new EditPatternElementDialog.EditPatternElementDialogContext(new ArrayList<>(), new ArrayList<>());

        var result = EditPatternElementDialog.doValidate(context, "anattributename", "adisplayname", "adescription");

        assertNull(result);
    }
}
