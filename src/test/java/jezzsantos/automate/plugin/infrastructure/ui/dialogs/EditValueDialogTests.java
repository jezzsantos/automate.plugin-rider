package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import com.intellij.openapi.ui.ValidationInfo;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("DialogTitleCapitalization")
public class EditValueDialogTests {

    @Test
    public void whenDoValidateAndIsNotRequiredAndNoValue_ThenReturnsNull() {

        var context = new EditValueDialog.EditValueDialogContext(false, null, null);

        var result = EditValueDialog.doValidate(context, "");

        assertNull(result);
    }

    @Test
    public void whenDoValidateAndIsRequiredAndNoValue_ThenReturnsError() {

        var context = new EditValueDialog.EditValueDialogContext(true, null, null);

        var result = EditValueDialog.doValidate(context, "");

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.EditValue.Validation.ValueMissing.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndHasValidatorAndValidatorReturnsNull_ThenReturnsNull() {

        var context = new EditValueDialog.EditValueDialogContext(true, null, x -> null);

        var result = EditValueDialog.doValidate(context, "avalue");

        assertNull(result);
    }

    @Test
    public void whenDoValidateAndHasValidatorAndValidatorReturnsError_ThenReturnsError() {

        var context = new EditValueDialog.EditValueDialogContext(true, null, x -> new ValidationInfo("amessage"));

        var result = EditValueDialog.doValidate(context, "avalue");

        assertNotNull(result);
        assertEquals("amessage", result.message);
        assertFalse(result.okEnabled);
    }
}
