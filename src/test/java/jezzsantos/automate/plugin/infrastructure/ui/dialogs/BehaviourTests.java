package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import com.intellij.openapi.ui.ComboBox;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BehaviourTests {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void whenConstructedWithStringAttribute_ThenSetComponentIsTextField() {

        var attribute = new Attribute("anid", "anattributename1", false, null, AutomateConstants.AttributeDataType.STRING, new ArrayList<>());
        var behaviour = new EditDraftElementDialog.Behaviour("aname", "avalue", attribute);

        var label = behaviour.getComponentLabel();
        var component = behaviour.getComponent();

        assertEquals("aname", label.getText());
        assertTrue(component instanceof JTextField);
        assertEquals("avalue", ((JTextField) component).getText());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void whenConstructedWithIntegerAttribute_ThenSetComponentIsTextField() {

        var attribute = new Attribute("anid", "anattributename1", false, null, AutomateConstants.AttributeDataType.INTEGER, new ArrayList<>());
        var behaviour = new EditDraftElementDialog.Behaviour("aname", "25", attribute);

        var label = behaviour.getComponentLabel();
        var component = behaviour.getComponent();

        assertEquals("aname", label.getText());
        assertTrue(component instanceof JTextField);
        assertEquals("25", ((JTextField) component).getText());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void whenConstructedWithFloatAttribute_ThenSetComponentIsTextField() {

        var attribute = new Attribute("anid", "anattributename1", false, null, AutomateConstants.AttributeDataType.FLOAT, new ArrayList<>());
        var behaviour = new EditDraftElementDialog.Behaviour("aname", "25.5", attribute);

        var label = behaviour.getComponentLabel();
        var component = behaviour.getComponent();

        assertEquals("aname", label.getText());
        assertTrue(component instanceof JTextField);
        assertEquals("25.5", ((JTextField) component).getText());
    }

    @Test
    public void whenConstructedWithBooleanAttribute_ThenSetComponentIsTextField() {

        var attribute = new Attribute("anid", "anattributename1", false, "true", AutomateConstants.AttributeDataType.BOOLEAN, new ArrayList<>());
        var behaviour = new EditDraftElementDialog.Behaviour("aname", "true", attribute);

        var label = behaviour.getComponentLabel();
        var component = behaviour.getComponent();

        assertNull(label);
        assertTrue(component instanceof JCheckBox);
        assertEquals("true", Boolean.toString(((JCheckBox) component).isSelected()));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void whenConstructedWithDateTimeAttribute_ThenSetComponentIsTextField() {

        var attribute = new Attribute("anid", "anattributename1", false, null, AutomateConstants.AttributeDataType.DATETIME, new ArrayList<>());
        var behaviour = new EditDraftElementDialog.Behaviour("aname", "avalue", attribute);

        var label = behaviour.getComponentLabel();
        var component = behaviour.getComponent();

        assertEquals("aname", label.getText());
        assertTrue(component instanceof JTextField);
        assertEquals("avalue", ((JTextField) component).getText());
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Test
    public void whenConstructedWithStringAttributeWithChoices_ThenSetComponentIsTextField() {

        var attribute = new Attribute("anid", "anattributename1", false, null, AutomateConstants.AttributeDataType.STRING, List.of("achoice1", "achoice2", "achoice3"));
        var behaviour = new EditDraftElementDialog.Behaviour("aname", "achoice2", attribute);

        var label = behaviour.getComponentLabel();
        var component = behaviour.getComponent();

        assertEquals("aname", label.getText());
        assertTrue(component instanceof ComboBox<?>);
        assertEquals("achoice2", ((ComboBox<String>) component).getSelectedItem());
    }

    @Test
    public void whenBuildUIAndNoAttributes_ThenCreatesFormControlsAndInitialises() {

        var patternElement = new PatternElement("anid", "aname");

        var context = new EditDraftElementDialog.EditDraftElementDialogContext(patternElement);
        var panel = new JPanel();

        EditDraftElementDialog.buildUI(context, panel);

        var components = panel.getComponents();
        assertEquals(1, panel.getComponentCount());
        var component1 = (JLabel) components[0];
        assertEquals(AutomateBundle.message("dialog.EditDraftElement.NoControls.Message"), component1.getText());
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void whenBuildUI_ThenCreatesFormControlsAndInitialises() {

        var patternElement = new PatternElement("anid", "aname");
        patternElement.addAttribute(new Attribute("anid", "anattributename1", false, "avalue", AutomateConstants.AttributeDataType.STRING, new ArrayList<>()));
        patternElement.addAttribute(new Attribute("anid", "anattributename2", false, "25", AutomateConstants.AttributeDataType.INTEGER, new ArrayList<>()));
        patternElement.addAttribute(new Attribute("anid", "anattributename3", false, "true", AutomateConstants.AttributeDataType.BOOLEAN, new ArrayList<>()));
        patternElement.addAttribute(new Attribute("anid", "anattributename4", false, "adatetime", AutomateConstants.AttributeDataType.DATETIME, new ArrayList<>()));
        patternElement.addAttribute(
          new Attribute("anid", "anattributename5", false, "achoice2", AutomateConstants.AttributeDataType.STRING, List.of("achoice1", "achoice2", "achoice3")));

        var context = new EditDraftElementDialog.EditDraftElementDialogContext(patternElement);
        var panel = new JPanel();

        EditDraftElementDialog.buildUI(context, panel);

        var components = panel.getComponents();
        assertEquals(9, panel.getComponentCount());
        var component1 = (JLabel) components[0];
        var component2 = (JTextField) components[1];
        assertEquals("anattributename1", component1.getText());
        assertEquals("avalue", component2.getText());
        var component3 = (JLabel) components[2];
        var component4 = (JTextField) components[3];
        assertEquals("anattributename2", component3.getText());
        assertEquals("25", component4.getText());
        var component5 = (JCheckBox) components[4];
        assertEquals("anattributename3", component5.getText());
        assertEquals("true", Boolean.toString(component5.isSelected()));
        var component6 = (JLabel) components[5];
        var component7 = (JTextField) components[6];
        assertEquals("anattributename4", component6.getText());
        assertEquals("adatetime", component7.getText());
        var component8 = (JLabel) components[7];
        var component9 = (ComboBox<String>) components[8];
        assertEquals("anattributename5", component8.getText());
        assertEquals("achoice2", component9.getSelectedItem());
    }

    @Test
    public void whenValidateAndIsRequiredAndMissing_ThenReturnsError() {

        var attribute = new Attribute("anid", "anattributename", true, null, AutomateConstants.AttributeDataType.STRING, null);

        var result = new EditDraftElementDialog.Behaviour("aname", null, attribute)
          .validate();

        assertFalse(result.okEnabled);
        assertEquals(AutomateBundle.message("dialog.EditDraftElement.Validation.RequiredAndMissing.Message", "aname"), result.message);
    }

    @Test
    public void whenValidateAndIsNotCorrectDataType_ThenReturnsError() {

        var attribute = new Attribute("anid", "anattributename", false, null, AutomateConstants.AttributeDataType.INTEGER, null);

        var result = new EditDraftElementDialog.Behaviour("aname", "notaninteger", attribute)
          .validate();

        assertFalse(result.okEnabled);
        assertEquals(AutomateBundle.message("dialog.EditDraftElement.Validation.InvalidDataType.Message", "aname", AutomateConstants.AttributeDataType.INTEGER), result.message);
    }

    @Test
    public void whenValidateAndValueIsNotAChoice_ThenReturnsError() {

        var attribute = new Attribute("anid", "anattributename", false, null, AutomateConstants.AttributeDataType.STRING, List.of("achoice1", "achoice2", "achoice3"));

        var result = new EditDraftElementDialog.Behaviour("aname", "notachoice", attribute)
          .validate();

        assertFalse(result.okEnabled);
        assertEquals(AutomateBundle.message("dialog.EditDraftElement.Validation.InvalidChoice.Message", "aname", "achoice1, achoice2, achoice3"), result.message);
    }

    @Test
    public void whenHasChangedAndNotFinalised_ThenReturnsFalse() {

        var result = new EditDraftElementDialog.Behaviour("aname", "avalue", new Attribute("anid", "aname"))
          .hasChangedValue();

        assertFalse(result);
    }

    @Test
    public void whenHasChangedAndNotChanged_ThenReturnsFalse() {

        var behaviour = new EditDraftElementDialog.Behaviour("aname", "avalue", new Attribute("anid", "aname"));
        behaviour.setFinalValue("avalue");

        var result = behaviour
          .hasChangedValue();

        assertFalse(result);
        assertEquals("avalue", behaviour.getFinalValue());
    }

    @Test
    public void whenHasChangedAndChanged_ThenReturnsTrue() {

        var behaviour = new EditDraftElementDialog.Behaviour("aname", "avalue", new Attribute("anid", "aname"));
        behaviour.setFinalValue("anewvalue");

        var result = behaviour
          .hasChangedValue();

        assertTrue(result);
        assertEquals("anewvalue", behaviour.getFinalValue());
    }
}
