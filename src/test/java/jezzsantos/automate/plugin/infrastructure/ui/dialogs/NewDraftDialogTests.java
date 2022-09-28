package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NewDraftDialogTests {

    @Test
    public void whenDoValidateAndToolkitIsEmpty_ThenReturnsError() {

        var context = new NewDraftDialog.NewDraftDialogContext(new ArrayList<>(), new ArrayList<>());

        var result = NewDraftDialog.doValidate(context, null, "");

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewDraft.ToolkitValidation.None.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndToolkitIsInvalid_ThenReturnsError() {

        var toolkit1 = new ToolkitLite("anid1", "atoolkitname1", "aversion1");
        var toolkit2 = new ToolkitLite("anid2", "atoolkitname2", "aversion2");
        var context = new NewDraftDialog.NewDraftDialogContext(new ArrayList<>(List.of(toolkit1)), new ArrayList<>());

        var result = NewDraftDialog.doValidate(context, toolkit2, "");

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewDraft.ToolkitValidation.None.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndEmptyName_ThenReturnsError() {

        var toolkit = new ToolkitLite("anid", "atoolkitname", "aversion");
        var context = new NewDraftDialog.NewDraftDialogContext(new ArrayList<>(List.of(toolkit)), new ArrayList<>());

        var result = NewDraftDialog.doValidate(context, toolkit, "");

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewDraft.NameValidation.NotMatch.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndInvalidName_ThenReturnsError() {

        var toolkit = new ToolkitLite("anid", "atoolkitname", "aversion");
        var context = new NewDraftDialog.NewDraftDialogContext(new ArrayList<>(List.of(toolkit)), new ArrayList<>());

        var result = NewDraftDialog.doValidate(context, toolkit, "^aninvalidname^");

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewDraft.NameValidation.NotMatch.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndNameIsReserved_ThenReturnsError() {

        var toolkit = new ToolkitLite("anid", "atoolkitname", "aversion");
        var draft = new DraftLite("anid", "adraftname", "atoolkitid", "aversion", false);
        var context = new NewDraftDialog.NewDraftDialogContext(new ArrayList<>(List.of(toolkit)), new ArrayList<>(List.of(draft)));

        var result = NewDraftDialog.doValidate(context, toolkit, "adraftname");

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewDraft.NameValidation.Exists.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidate_ThenReturnsNull() {

        var toolkit = new ToolkitLite("anid", "atoolkitname", "aversion");
        var context = new NewDraftDialog.NewDraftDialogContext(new ArrayList<>(List.of(toolkit)), new ArrayList<>());

        var result = NewDraftDialog.doValidate(context, toolkit, "adraftname");

        assertNull(result);
    }
}
