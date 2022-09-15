package jezzsantos.automate.plugin.application.interfaces.drafts;

import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DraftElementSchemaTests {

    @Test
    public void whenListMissingElementsAndNoElements_ThenReturnsEmpty() {

        var schema = new DraftElementSchema(new PatternElement("anid", "aname"));
        var element = new DraftElement("aname", new HashMap<>(), false);

        var result = schema.listMissingElements(element);

        assertEquals(0, result.size());
    }

    @Test
    public void whenListMissingElementsAndHasElements_ThenReturnsElementsSchema() {

        var element1 = new PatternElement("anelementid1", "anelementname1");
        var element2 = new PatternElement("anelementid2", "anelementname2");
        var patternElement = new PatternElement("anid", "aname");
        patternElement.addElement(element1);
        patternElement.addElement(element2);
        var schema = new DraftElementSchema(patternElement);
        var element = new DraftElement("aname", new HashMap<>(), false);

        var result = schema.listMissingElements(element);

        assertEquals(2, result.size());
        assertEquals(List.of(element1, element2), result);
    }

    @Test
    public void whenListMissingElementsAndHasElementsOfSingularCardinalityThatDontExistInDraftElement_ThenReturnsElementsSchema() {

        var element1 = new PatternElement("anelementid1", "anelementname1", AutomateConstants.ElementCardinality.ONE);
        var element2 = new PatternElement("anelementid2", "anelementname2", AutomateConstants.ElementCardinality.ZERO_OR_ONE);
        var element3 = new PatternElement("anelementid3", "anelementname3", AutomateConstants.ElementCardinality.ZERO_OR_MANY);
        var element4 = new PatternElement("anelementid4", "anelementname4", AutomateConstants.ElementCardinality.ONE_OR_MANY);
        var patternElement = new PatternElement("anid", "aname");
        patternElement.addElement(element1);
        patternElement.addElement(element2);
        patternElement.addElement(element3);
        patternElement.addElement(element4);
        var schema = new DraftElementSchema(patternElement);
        var element = new DraftElement("aname", new HashMap<>(), false);

        var result = schema.listMissingElements(element);

        assertEquals(4, result.size());
        assertEquals(List.of(element1, element2, element3, element4), result);
    }

    @Test
    public void whenListMissingElementsAndHasElementsOfSingularCardinalityThatExistInDraftElement_ThenReturnsEmpty() {

        var element1 = new PatternElement("anelementid1", "anelementname1", AutomateConstants.ElementCardinality.ONE);
        var element2 = new PatternElement("anelementid2", "anelementname2", AutomateConstants.ElementCardinality.ZERO_OR_ONE);
        var element3 = new PatternElement("anelementid3", "anelementname3", AutomateConstants.ElementCardinality.ZERO_OR_MANY);
        var element4 = new PatternElement("anelementid4", "anelementname4", AutomateConstants.ElementCardinality.ONE_OR_MANY);
        var patternElement = new PatternElement("anid", "aname");
        patternElement.addElement(element1);
        patternElement.addElement(element2);
        patternElement.addElement(element3);
        patternElement.addElement(element4);
        var schema = new DraftElementSchema(patternElement);
        var childElements = Map.of(
          "anelementname1", createDraftElementValueWithSchema("anelementname1", "anelementid1"),
          "anelementname2", createDraftElementValueWithSchema("anelementname2", "anelementid2"),
          "anelementname3", createDraftElementValueWithSchema("anelementname3", "anelementid3"),
          "anelementname4", createDraftElementValueWithSchema("anelementname4", "anelementid4")
        );
        var element = new DraftElement("aname", childElements, false);

        var result = schema.listMissingElements(element);

        assertEquals(2, result.size());
        assertEquals(List.of(element3, element4), result);
    }

    private DraftElementValue createDraftElementValueWithSchema(String name, String schemaId) {

        var properties = Map.of(
          "Schema", new DraftElementValue("Schema", Map.of(
            "Id", new DraftElementValue(schemaId),
            "Type", new DraftElementValue("atype")
          ))
        );

        return new DraftElementValue(name, properties);
    }
}
