package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NewAttributeDialogTests {

    @Test
    public void whenDoValidateAndNameIsEmpty_ThenReturnsError() {

        var context = new NewPatternAttributeDialog.NewPatternAttributeDialogContext(new ArrayList<>(), new ArrayList<>());

        var result = NewPatternAttributeDialog.doValidate(context, "", null, "", new ArrayList<>());

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewAttribute.NameValidation.NotMatch.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndNameIsInvalid_ThenReturnsError() {

        var context = new NewPatternAttributeDialog.NewPatternAttributeDialogContext(new ArrayList<>(), new ArrayList<>());

        var result = NewPatternAttributeDialog.doValidate(context, "^aninvalidname^", null, "", new ArrayList<>());

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewAttribute.NameValidation.NotMatch.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndNameIsReserved_ThenReturnsError() {

        var context = new NewPatternAttributeDialog.NewPatternAttributeDialogContext(new ArrayList<>(List.of(new Attribute("anid", "anattributename"))), new ArrayList<>());

        var result = NewPatternAttributeDialog.doValidate(context, "anattributename", null, "", new ArrayList<>());

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewAttribute.NameValidation.Exists.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndDataTypeIsEmpty_ThenReturnsError() {

        var context = new NewPatternAttributeDialog.NewPatternAttributeDialogContext(new ArrayList<>(), new ArrayList<>(List.of(AutomateConstants.AttributeDataType.STRING)));

        var result = NewPatternAttributeDialog.doValidate(context, "aname", null, "", new ArrayList<>());

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewAttribute.DataTypeValidation.NotMatch.Message", AutomateConstants.AttributeDataType.STRING.getDisplayName()),
                     result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndDataTypeIsInvalid_ThenReturnsError() {

        var context = new NewPatternAttributeDialog.NewPatternAttributeDialogContext(new ArrayList<>(), new ArrayList<>(List.of(AutomateConstants.AttributeDataType.STRING)));

        var result = NewPatternAttributeDialog.doValidate(context, "aname", AutomateConstants.AttributeDataType.INTEGER, "", new ArrayList<>());

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewAttribute.DataTypeValidation.NotMatch.Message", AutomateConstants.AttributeDataType.STRING.getDisplayName()),
                     result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndDefaultValueNotMatchDataType_ThenReturnsError() {

        var context = new NewPatternAttributeDialog.NewPatternAttributeDialogContext(new ArrayList<>(), new ArrayList<>(List.of(AutomateConstants.AttributeDataType.INTEGER)));

        var result = NewPatternAttributeDialog.doValidate(context, "aname", AutomateConstants.AttributeDataType.INTEGER, "notanintegervalue", new ArrayList<>());

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewAttribute.DefaultValueValidation.NotDataType.Message", AutomateConstants.AttributeDataType.INTEGER.getDisplayName()),
                     result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndDefaultValueIsNotAChoice_ThenReturnsError() {

        var context = new NewPatternAttributeDialog.NewPatternAttributeDialogContext(new ArrayList<>(), new ArrayList<>(List.of(AutomateConstants.AttributeDataType.STRING)));

        var result = NewPatternAttributeDialog.doValidate(context, "aname", AutomateConstants.AttributeDataType.STRING, "notachoice",
                                                          new ArrayList<>(List.of("achoice1", "achoice2", "achoice3")));

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.NewAttribute.DefaultValueValidation.NotAChoice.Message"), result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidateAndAChoiceNotMatchDataType_ThenReturnsError() {

        var context = new NewPatternAttributeDialog.NewPatternAttributeDialogContext(new ArrayList<>(), new ArrayList<>(List.of(AutomateConstants.AttributeDataType.INTEGER)));

        var result = NewPatternAttributeDialog.doValidate(context, "aname", AutomateConstants.AttributeDataType.INTEGER, "", new ArrayList<>(List.of("1", "notaninteger", "3")));

        assertNotNull(result);
        assertEquals(
          AutomateBundle.message("dialog.NewAttribute.ChoicesValidation.NotDataType.Message", "notaninteger", AutomateConstants.AttributeDataType.INTEGER.getDisplayName()),
          result.message);
        assertFalse(result.okEnabled);
    }

    @Test
    public void whenDoValidate_ThenReturnsNull() {

        var context = new NewPatternAttributeDialog.NewPatternAttributeDialogContext(new ArrayList<>(), new ArrayList<>(List.of(AutomateConstants.AttributeDataType.STRING)));

        var result = NewPatternAttributeDialog.doValidate(context, "aname", AutomateConstants.AttributeDataType.STRING, "adefaultvalue", new ArrayList<>());

        assertNull(result);
    }
}
