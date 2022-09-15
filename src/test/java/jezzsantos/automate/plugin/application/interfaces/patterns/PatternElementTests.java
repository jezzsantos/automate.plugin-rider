package jezzsantos.automate.plugin.application.interfaces.patterns;

import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PatternElementTests {

    @Test
    public void whenIsRootAndNotRoot_ThenReturnsFalse() {

        var element = new PatternElement("anid", "aname");

        var result = element.isRoot();

        assertFalse(result);
    }

    @Test
    public void whenIsRootAndIsRoot_ThenReturnsTrue() {

        var element = new PatternElement("anid", "aname");
        element.setRoot();

        var result = element.isRoot();

        assertTrue(result);
    }

    @Test
    public void whenGetDisplayNameAndHasNone_ThenReturnsName() {

        var element = new PatternElement("anid", "aname");

        var result = element.getDisplayName();

        assertEquals("aname", result);
    }

    @Test
    public void whenGetDisplayName_ThenReturnsDisplayName() {

        var element = new PatternElement("anid", "aname");
        element.setDisplayName("adisplayname");

        var result = element.getDisplayName();

        assertEquals("adisplayname", result);
    }

    @Test
    public void whenGetCodeTemplatesAndHasNone_ThenReturnsEmpty() {

        var element = new PatternElement("anid", "aname");

        var result = element.getCodeTemplates();

        assertTrue(result.isEmpty());
    }

    @Test
    public void whenGetCodeTemplatesAndHasSome_ThenReturnsOrderedByName() {

        var element = new PatternElement("anid", "aname");
        var codeTemplate1 = new CodeTemplate("anid1", "aname2");
        var codeTemplate2 = new CodeTemplate("anid2", "aname3");
        var codeTemplate3 = new CodeTemplate("anid3", "aname1");
        element.addCodeTemplate(codeTemplate1);
        element.addCodeTemplate(codeTemplate2);
        element.addCodeTemplate(codeTemplate3);

        var result = element.getCodeTemplates();

        assertEquals(List.of(codeTemplate3, codeTemplate1, codeTemplate2), result);
    }

    @Test
    public void whenGetAutomationsAndHasNone_ThenReturnsEmpty() {

        var element = new PatternElement("anid", "aname");

        var result = element.getAutomation();

        assertTrue(result.isEmpty());
    }

    @Test
    public void whenGetAutomationsAndHasSome_ThenReturnsOrderedByName() {

        var element = new PatternElement("anid", "aname");
        var automation1 = new Automation("anid1", "aname2");
        var automation2 = new Automation("anid2", "aname3");
        var automation3 = new Automation("anid3", "aname1");
        element.addAutomation(automation1);
        element.addAutomation(automation2);
        element.addAutomation(automation3);

        var result = element.getAutomation();

        assertEquals(List.of(automation3, automation1, automation2), result);
    }

    @Test
    public void whenGetAttributesAndHasNone_ThenReturnsEmpty() {

        var element = new PatternElement("anid", "aname");

        var result = element.getAttributes();

        assertTrue(result.isEmpty());
    }

    @Test
    public void whenGetAttributesAndHasSome_ThenReturnsOrderedByName() {

        var element = new PatternElement("anid", "aname");
        var attribute1 = new Attribute("anid1", "aname2");
        var attribute2 = new Attribute("anid2", "aname3");
        var attribute3 = new Attribute("anid3", "aname1");
        element.addAttribute(attribute1);
        element.addAttribute(attribute2);
        element.addAttribute(attribute3);

        var result = element.getAttributes();

        assertEquals(List.of(attribute3, attribute1, attribute2), result);
    }

    @Test
    public void whenGetElementsAndHasNone_ThenReturnsEmpty() {

        var element = new PatternElement("anid", "aname");

        var result = element.getElements();

        assertTrue(result.isEmpty());
    }

    @Test
    public void whenGetElementsAndHasSome_ThenReturnsOrderedByDisplayName() {

        var element = new PatternElement("arootid", "aname");
        var element1 = new PatternElement("anid1", "aname2");
        element1.setDisplayName("adisplayname1");
        var element2 = new PatternElement("anid2", "aname3");
        element2.setDisplayName("adisplayname2");
        var element3 = new PatternElement("anid3", "aname1");
        element3.setDisplayName("adisplayname3");
        element.addElement(element1);
        element.addElement(element2);
        element.addElement(element3);

        var result = element.getElements();

        assertEquals(List.of(element1, element2, element3), result);
    }

    @Test
    public void whenFindSchemaAndNotExistsInHierarchy_ThenThrows() {

        var element = new PatternElement("anid", "aname");

        var exception = assertThrows(RuntimeException.class, () -> element.findSchema("aschemaid"));

        assertEquals(AutomateBundle.message("exception.PatternElement.FindSchema.NotExists.Message", "aschemaid"), exception.getMessage());
    }

    @Test
    public void whenFindSchemaAndIsRoot_ThenReturnsRoot() {

        var rootElement = new PatternElement("arootid", "aname");

        var result = rootElement.findSchema("arootid");

        assertEquals(rootElement, result.getSchema());
    }

    @Test
    public void whenFindSchemaAndIsDescendantOfRoot_ThenReturnsDescendant() {

        var grandChildElement = new PatternElement("adescendantid", "aname");
        var childElement = new PatternElement("achildid", "aname");
        var rootElement = new PatternElement("arootid", "aname");
        childElement.addElement(grandChildElement);
        rootElement.addElement(childElement);

        var result = rootElement.findSchema("adescendantid");

        assertEquals(grandChildElement, result.getSchema());
    }

    @Test
    public void whenToStringAndIsRoot_ThenReturnsString() {

        var rootElement = new PatternElement("arootid", "aname");

        var result = rootElement.toString();

        assertEquals("aname (required)", result);
    }

    @Test
    public void whenToStringAndIsRequiredDescendant_ThenReturnsString() {

        var grandChildElement = new PatternElement("adescendantid", "aname", AutomateConstants.ElementCardinality.ONE);
        var childElement = new PatternElement("achildid", "aname");
        var rootElement = new PatternElement("arootid", "aname");
        childElement.addElement(grandChildElement);
        rootElement.addElement(childElement);

        var result = grandChildElement.toString();

        assertEquals("aname (required)", result);
    }

    @Test
    public void whenToStringAndIsOptionalDescendant_ThenReturnsString() {

        var grandChildElement = new PatternElement("adescendantid", "aname", AutomateConstants.ElementCardinality.ZERO_OR_ONE);
        var childElement = new PatternElement("achildid", "aname");
        var rootElement = new PatternElement("arootid", "aname");
        childElement.addElement(grandChildElement);
        rootElement.addElement(childElement);

        var result = grandChildElement.toString();

        assertEquals("aname (optional)", result);
    }

    @Test
    public void whenToStringAndIsOptionalDescendantCollection_ThenReturnsString() {

        var grandChildElement = new PatternElement("adescendantid", "aname", AutomateConstants.ElementCardinality.ZERO_OR_MANY);
        var childElement = new PatternElement("achildid", "aname");
        var rootElement = new PatternElement("arootid", "aname");
        childElement.addElement(grandChildElement);
        rootElement.addElement(childElement);

        var result = grandChildElement.toString();

        assertEquals("aname (collection, optional)", result);
    }

    @Test
    public void whenToStringAndIsRequiredDescendantCollection_ThenReturnsString() {

        var grandChildElement = new PatternElement("adescendantid", "aname", AutomateConstants.ElementCardinality.ONE_OR_MANY);
        var childElement = new PatternElement("achildid", "aname");
        var rootElement = new PatternElement("arootid", "aname");
        childElement.addElement(grandChildElement);
        rootElement.addElement(childElement);

        var result = grandChildElement.toString();

        assertEquals("aname (collection, required)", result);
    }
}
