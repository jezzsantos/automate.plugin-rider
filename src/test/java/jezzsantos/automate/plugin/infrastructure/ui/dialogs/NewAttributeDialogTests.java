package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NewAttributeDialogTests {

    @Test
    public void whenDoValidateAndNameIsEmpty_ThenReturnsError() {

        var context = new NewAttributeDialogContext(new ArrayList<>(), new ArrayList<>());

        var result = NewAttributeDialog.doValidate(context, "", "", "", new ArrayList<>());

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewAttribute.NameValidation.NotMatch.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndNameIsInvalid_ThenReturnsError() {

        var context = new NewAttributeDialogContext(new ArrayList<>(), new ArrayList<>());

        var result = NewAttributeDialog.doValidate(context, "^aninvalidname^", "", "", new ArrayList<>());

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewAttribute.NameValidation.NotMatch.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndNameIsReserved_ThenReturnsError() {

        var context = new NewAttributeDialogContext(new ArrayList<>(List.of(new Attribute("anid", "anattributename"))), new ArrayList<>());

        var result = NewAttributeDialog.doValidate(context, "anattributename", "", "", new ArrayList<>());

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewAttribute.NameValidation.Exists.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndDataTypeIsEmpty_ThenReturnsError() {

        var context = new NewAttributeDialogContext(new ArrayList<>(), new ArrayList<>(List.of("adatatype1")));

        var result = NewAttributeDialog.doValidate(context, "aname", "", "", new ArrayList<>());

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewAttribute.DataTypeValidation.NotMatch.Message", "adatatype1"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndDataTypeIsInvalid_ThenReturnsError() {

        var context = new NewAttributeDialogContext(new ArrayList<>(), new ArrayList<>(List.of("adatatype1")));

        var result = NewAttributeDialog.doValidate(context, "aname", "aninvaliddatatype", "", new ArrayList<>());

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewAttribute.DataTypeValidation.NotMatch.Message", "adatatype1"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndDefaultValueNotMatchDataType_ThenReturnsError() {

        var context = new NewAttributeDialogContext(new ArrayList<>(), new ArrayList<>(List.of("int")));

        var result = NewAttributeDialog.doValidate(context, "aname", "int", "notanintegervalue", new ArrayList<>());

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewAttribute.DefaultValueValidation.NotDataType.Message", "int"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndDefaultValueIsNotAChoice_ThenReturnsError() {

        var context = new NewAttributeDialogContext(new ArrayList<>(), new ArrayList<>(List.of("string")));

        var result = NewAttributeDialog.doValidate(context, "aname", "string", "notachoice", new ArrayList<>(List.of("achoice1", "achoice2", "achoice3")));

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewAttribute.DefaultValueValidation.NotAChoice.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndAChoiceNotMatchDataType_ThenReturnsError() {

        var context = new NewAttributeDialogContext(new ArrayList<>(), new ArrayList<>(List.of("int")));

        var result = NewAttributeDialog.doValidate(context, "aname", "int", "", new ArrayList<>(List.of("1", "notaninteger", "3")));

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewAttribute.ChoicesValidation.NotDataType.Message", "notaninteger", "int"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidate_ThenReturnsNull() {

        var context = new NewAttributeDialogContext(new ArrayList<>(), new ArrayList<>(List.of("string")));

        var result = NewAttributeDialog.doValidate(context, "aname", "string", "adefaultvalue", new ArrayList<>());

        assertNull(result);
    }
}
