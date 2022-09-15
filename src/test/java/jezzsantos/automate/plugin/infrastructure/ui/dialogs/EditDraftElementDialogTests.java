package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import com.intellij.openapi.ui.ComboBox;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EditDraftElementDialogTests {

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
    public void whenDoValidateAndEverythingValid_ThenReturnsNull() {

        var patternElement = new PatternElement("anid", "aname");
        patternElement.addAttribute(new Attribute("anid", "anattributename", false, null, AutomateConstants.AttributeDataType.STRING, new ArrayList<>()));
        var context = new EditDraftElementDialog.EditDraftElementDialogContext(patternElement);

        var result = EditDraftElementDialog.doValidate(context);

        assertNull(result);
    }

    @Test
    public void whenDoValidateAndValueIsRequiredAnDMissing_ThenReturnsError() {

        var patternElement = new PatternElement("anid", "aname");
        patternElement.addAttribute(new Attribute("anid", "anattributename", true, null, AutomateConstants.AttributeDataType.STRING, new ArrayList<>()));
        var context = new EditDraftElementDialog.EditDraftElementDialogContext(patternElement);

        var result = EditDraftElementDialog.doValidate(context);

        assertNotNull(result);
        assertEquals(AutomateBundle.message("dialog.EditDraftElement.Validation.RequiredAndMissing.Message", "anattributename"), result.message);
        assertFalse(result.okEnabled);
    }
}


