package jezzsantos.automate.plugin.infrastructure.ui.dialogs.drafts;

import com.intellij.openapi.ui.ComboBox;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElement;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElementValue;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Nested
    class EditDraftElementDialogContextTests {

        @SuppressWarnings("ConstantConditions")
        @Test
        public void whenConstructedWithDraftElementAndNoConfiguredProperties_ThenCreatesAllBehavioursWithDefaultValues() {

            var schema = new PatternElement("anid", "aname");
            var attribute1 = new Attribute("anattributeid1", "anattributename1", false, "adefaultvalue1", null, null);
            var attribute2 = new Attribute("anattributeid2", "anattributename2", false, "adefaultvalue2", null, null);
            var attribute3 = new Attribute("anattributeid3", "anattributename3", false, "adefaultvalue3", null, null);
            schema.addAttribute(attribute1);
            schema.addAttribute(attribute2);
            schema.addAttribute(attribute3);
            var element = new DraftElement("aname", Map.of(), false);

            var context = new EditDraftElementDialog.EditDraftElementDialogContext(element, schema);
            var result = context.getBehaviours();

            assertEquals(3, result.size());
            assertEquals("anattributename1", result.get("anattributename1").getComponentLabel().getText());
            assertEquals("adefaultvalue1", ((JTextField) result.get("anattributename1").getComponent()).getText());
            assertEquals("anattributename2", result.get("anattributename2").getComponentLabel().getText());
            assertEquals("adefaultvalue2", ((JTextField) result.get("anattributename2").getComponent()).getText());
            assertEquals("anattributename3", result.get("anattributename3").getComponentLabel().getText());
            assertEquals("adefaultvalue3", ((JTextField) result.get("anattributename3").getComponent()).getText());
        }

        @SuppressWarnings("ConstantConditions")
        @Test
        public void whenConstructedWithDraftElementSomeConfiguredProperties_ThenCreatesAllBehavioursWithValuesAndDefaults() {

            var schema = new PatternElement("anid", "aname");
            var attribute1 = new Attribute("anattributeid1", "anattributename1", false, "adefaultvalue1", null, null);
            var attribute2 = new Attribute("anattributeid2", "anattributename2", false, "adefaultvalue2", null, null);
            var attribute3 = new Attribute("anattributeid3", "anattributename3", false, "adefaultvalue3", null, null);
            schema.addAttribute(attribute1);
            schema.addAttribute(attribute2);
            schema.addAttribute(attribute3);
            var element = new DraftElement("aname", Map.of(
              "anattributename2", new DraftElementValue("avalue2")
            ), false);

            var context = new EditDraftElementDialog.EditDraftElementDialogContext(element, schema);
            var result = context.getBehaviours();

            assertEquals(3, result.size());
            assertEquals("anattributename1", result.get("anattributename1").getComponentLabel().getText());
            assertEquals("adefaultvalue1", ((JTextField) result.get("anattributename1").getComponent()).getText());
            assertEquals("anattributename2", result.get("anattributename2").getComponentLabel().getText());
            assertEquals("avalue2", ((JTextField) result.get("anattributename2").getComponent()).getText());
            assertEquals("anattributename3", result.get("anattributename3").getComponentLabel().getText());
            assertEquals("adefaultvalue3", ((JTextField) result.get("anattributename3").getComponent()).getText());
        }
    }
}


