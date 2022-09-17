package jezzsantos.automate.plugin.application.interfaces.drafts;

import jezzsantos.automate.core.AutomateConstants;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("ConstantConditions")
public class DraftElementTests {

    @Test
    public void whenGetName_ThenReturnsName() {

        var element = new DraftElement("apropertyname", new HashMap<>(), false);

        var result = element.getName();

        assertEquals("apropertyname", result);
    }

    @Test
    public void whenContainsKeyAndNotExists_ThenReturnsFalse() {

        var element = new DraftElement("apropertyname", new HashMap<>(), false);

        var result = element.containsKey("anunknownkey");

        assertFalse(result);
    }

    @Test
    public void whenContainsKeyAndExists_ThenReturnsTrue() {

        var map = new HashMap<String, DraftElementValue>();
        map.put("akey", new DraftElementValue("avalue"));

        var element = new DraftElement("apropertyname", map, false);

        var result = element.containsKey("akey");

        assertTrue(result);
    }

    @Test
    public void whenGetPropertyAndNotExists_ThenReturnsNull() {

        var element = new DraftElement("apropertyname", new HashMap<>(), false);

        var result = element.getProperty("aname");

        assertNull(result);
    }

    @Test
    public void whenGetPropertyAndExists_ThenReturnsProperty() {

        var value = new DraftElementValue("avalue");
        var map = new HashMap<String, DraftElementValue>();
        map.put("aname", value);

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getProperty("aname");

        assertEquals("aname", result.getName());
        assertEquals("avalue", result.getValue());
    }

    @Test
    public void whenGetIdAndNotExists_ThenReturnsNull() {

        var value = new DraftElementValue("avalue");
        var map = new HashMap<String, DraftElementValue>();
        map.put("aname", value);

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getId();

        assertNull(result);
    }

    @Test
    public void whenGetIdExists_ThenReturnsId() {

        var value = new DraftElementValue("anid");
        var map = new HashMap<String, DraftElementValue>();
        map.put("Id", value);

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getId();

        assertEquals("anid", result);
    }

    @Test
    public void whenGetPathAndNotExists_ThenReturnsNull() {

        var value = new DraftElementValue("avalue");
        var map = new HashMap<String, DraftElementValue>();
        map.put("aname", value);

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getConfigurePath();

        assertNull(result);
    }

    @Test
    public void whenGetPathExists_ThenReturnsId() {

        var value = new DraftElementValue("avalue");
        var map = new HashMap<String, DraftElementValue>();
        map.put("ConfigurePath", value);

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getConfigurePath();

        assertEquals("avalue", result);
    }

    @Test
    public void whenGetSchemaIdAndNotExists_ThenReturnsNull() {

        var value = new DraftElementValue("avalue");
        var map = new HashMap<String, DraftElementValue>();
        map.put("aname", value);

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getSchemaId();

        assertNull(result);
    }

    @Test
    public void whenGetSchemaIdExists_ThenReturnsId() {

        var value = new DraftElementValue("Schema", Map.of(
          "Type", new DraftElementValue("atype"),
          "Id", new DraftElementValue("anid")));
        var map = new HashMap<String, DraftElementValue>();
        map.put("Schema", value);

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getSchemaId();

        assertEquals("anid", result);
    }

    @Test
    public void whenIsSchemaTypeAndNotExists_ThenReturnsFalse() {

        var value = new DraftElementValue("avalue");
        var map = new HashMap<String, DraftElementValue>();
        map.put("aname", value);

        var element = new DraftElement("apropertyname", map, false);

        var result = element.isSchemaType(AutomateConstants.SchemaType.ELEMENT);

        assertFalse(result);
    }

    @Test
    public void whenIsSchemaTypeAndExistsAndNotMatch_ThenReturnsFalse() {

        var value = new DraftElementValue("Schema", Map.of(
          "Type", new DraftElementValue(AutomateConstants.SchemaType.ELEMENT.getValue()),
          "Id", new DraftElementValue("anid")));
        var map = new HashMap<String, DraftElementValue>();
        map.put("Schema", value);

        var element = new DraftElement("apropertyname", map, false);

        var result = element.isSchemaType(AutomateConstants.SchemaType.COLLECTIONITEM);

        assertFalse(result);
    }

    @Test
    public void whenIsSchemaTypeMatches_ThenReturnsTrue() {

        var value = new DraftElementValue("Schema", Map.of(
          "Type", new DraftElementValue(AutomateConstants.SchemaType.ELEMENT.getValue()),
          "Id", new DraftElementValue("anid")));
        var map = new HashMap<String, DraftElementValue>();
        map.put("Schema", value);

        var element = new DraftElement("apropertyname", map, false);

        var result = element.isSchemaType(AutomateConstants.SchemaType.ELEMENT);

        assertTrue(result);
    }

    @Test
    public void whenGetPropertiesAndIsEmpty_ThenReturnsEmpty() {

        var map = new HashMap<String, DraftElementValue>();

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getProperties();

        assertTrue(result.isEmpty());
    }

    @Test
    public void whenGetPropertiesAndHasOnlyBuiltInProperties_ThenReturnsEmpty() {

        var map = new HashMap<String, DraftElementValue>();
        map.put("Id", new DraftElementValue("anid"));
        map.put("Items", new DraftElementValue("anid"));
        map.put("ConfigurePath", new DraftElementValue("anid"));

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getProperties();

        assertTrue(result.isEmpty());
    }

    @Test
    public void whenGetPropertiesAndHasMoreThanBuiltInProperties_ThenReturnsMore() {

        var map = new HashMap<String, DraftElementValue>();
        map.put("Id", new DraftElementValue("anid"));
        map.put("Items", new DraftElementValue("avalue"));
        map.put("ConfigurePath", new DraftElementValue("avalue"));
        map.put("aname", new DraftElementValue("avalue"));

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getProperties();

        assertEquals(1, result.size());
        assertEquals("avalue", result.get("aname").getValue());
    }

    @Test
    public void whenGetElementAndNotExist_ThenReturnsNull() {

        var map = new HashMap<String, DraftElementValue>();

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getElement("aname");

        assertNull(result);
    }

    @Test
    public void whenGetElementAndExist_ThenReturnsElement() {

        var value = new DraftElementValue("aname", new HashMap<>());
        var map = new HashMap<String, DraftElementValue>();
        map.put("aname", value);

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getElement("aname");

        assertNotNull(result);
    }

    @Test
    public void whenGetCollectionItemsAndNotExist_ThenReturnsEmpty() {

        var map = new HashMap<String, DraftElementValue>();

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getCollectionItems();

        assertTrue(result.isEmpty());
    }

    @Test
    public void whenGetCollectionItemsAndExist_ThenReturnsItems() {

        var list = List.of(new DraftElement("apropertyname", Map.of("aname", new DraftElementValue("avalue")), false));
        var value = new DraftElementValue(list);
        var map = new HashMap<String, DraftElementValue>();
        map.put("Items", value);

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getCollectionItems();

        assertEquals(1, result.size());
        assertEquals("avalue", result.get(0).getProperty("aname").getValue());
    }

    @Test
    public void whenGetCollectionsAndNone_ThenReturnsEmpty() {

        var map = new HashMap<String, DraftElementValue>();

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getCollections();

        assertTrue(result.isEmpty());
    }

    @Test
    public void whenGetCollectionsAndHasOne_ThenReturnsCollections() {

        var property1 = new DraftElementValue("acollectionid");
        var property2 = new DraftElementValue("avalue");
        var value = new DraftElementValue("aname", Map.of("Id", property1, "Items", property2));
        var map = new HashMap<String, DraftElementValue>();
        map.put("acollectionname", value);

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getCollections();

        assertEquals(1, result.size());
        assertEquals("acollectionid", result.get("acollectionname").getProperty("Id").getValue());
    }

    @Test
    public void whenGetElementsAndNone_ThenReturnsEmpty() {

        var map = new HashMap<String, DraftElementValue>();

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getElements();

        assertTrue(result.isEmpty());
    }

    @Test
    public void whenGetElementsAndHasOne_ThenReturnsElements() {

        var property1 = new DraftElementValue("anelementid");
        var value = new DraftElementValue("aname", Map.of("Id", property1));
        var map = new HashMap<String, DraftElementValue>();
        map.put("anelementname", value);

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getElements();

        assertEquals(1, result.size());
        assertEquals("anelementid", result.get("anelementname").getProperty("Id").getValue());
    }

    @Test
    public void whenGetElementsAndHasMoreThanSchema_ThenReturnsMore() {

        var property1 = new DraftElementValue("anelementid");
        var value = new DraftElementValue("aname", Map.of("Id", property1));
        var map = new HashMap<String, DraftElementValue>();
        map.put("anelementname", value);
        map.put("Schema", new DraftElementValue("avalue"));

        var element = new DraftElement("apropertyname", map, false);

        var result = element.getElements();

        assertEquals(1, result.size());
        assertEquals("anelementid", result.get("anelementname").getProperty("Id").getValue());
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void whenEqualsAndSameInstance_ThenReturnsTrue() {

        var element = new DraftElement("aname", new HashMap<>(), false);

        var result = element.equals(element);

        assertTrue(result);
    }

    @Test
    public void whenEqualsWithNull_ThenReturnsTrue() {

        var element = new DraftElement("aname", new HashMap<>(), false);

        var result = element.equals(null);

        assertFalse(result);
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Test
    public void whenEqualsAndDifferentType_ThenReturnsFalse() {

        var otherObject = new DraftDetailed("anid", "aname", new HashMap<>());
        var element = new DraftElement("aname", new HashMap<>(), false);

        var result = element
          .equals(otherObject);

        assertFalse(result);
    }

    @Test
    public void whenEqualsAndNoIdentifiers_ThenReturnsFalse() {

        var otherElement = new DraftElement("aname", new HashMap<>(), false);
        var element = new DraftElement("aname", new HashMap<>(), false);

        var result = element
          .equals(otherElement);

        assertFalse(result);
    }

    @Test
    public void whenEqualsAndDifferentIds_ThenReturnsFalse() {

        var otherElement = new DraftElement("aname", Map.of(
          "Id", new DraftElementValue("anid1")), false);
        var element = new DraftElement("aname", Map.of(
          "Id", new DraftElementValue("anid2")), false);

        var result = element
          .equals(otherElement);

        assertFalse(result);
    }

    @Test
    public void whenEqualsAndDifferentInstancesButSameIds_ThenReturnsTrue() {

        var otherElement = new DraftElement("aname", Map.of(
          "Id", new DraftElementValue("anid")), false);
        var element = new DraftElement("aname", Map.of(
          "Id", new DraftElementValue("anid")), false);

        var result = element
          .equals(otherElement);

        assertTrue(result);
    }

    @Test
    public void whenDeleteElementAndNotExist_ThenDoesNothing() {

        var element = new DraftElement("aname", Map.of(
          "Id", new DraftElementValue("anid")), false);

        assertEquals(0, element.getElements().size());

        element.deleteElement("achildid");

        assertEquals(0, element.getElements().size());
    }

    @Test
    public void whenDeleteElementAndExists_ThenRemoves() {

        var element = new DraftElement("aname", new HashMap<>() {{
            put("anelementname", new DraftElementValue("achildelementname", Map.of(
              "Id", new DraftElementValue("achildid")
            )));
        }}, false);

        assertEquals(1, element.getElements().size());

        element.deleteElement("achildid");

        assertEquals(0, element.getElements().size());
    }

    @Test
    public void whenDeleteDescendantCollectionItemAndNoCollections_ThenDoesNothing() {

        var element = new DraftElement("aname", Map.of(
          "Id", new DraftElementValue("anid")), false);

        assertEquals(0, element.getCollections().size());

        element.deleteDescendantCollectionItem("acollectionitemid2");

        assertEquals(0, element.getCollections().size());
    }

    @Test
    public void whenDeleteDescendantCollectionItemAndItemNotExists_ThenDoesNothing() {

        var element = new DraftElement("aname", Map.of(
          "acollectionname1", new DraftElementValue("acollectionname1", Map.of(
            "Id", new DraftElementValue("acollectionid1"),
            "Items", new DraftElementValue(List.of()))),
          "acollectionname2", new DraftElementValue("acollectionname2", Map.of(
            "Id", new DraftElementValue("acollectionid2"),
            "Items", new DraftElementValue(List.of())))), false);

        assertEquals(2, element.getCollections().size());
        assertEquals(0, element.getCollections().get(0).getCollectionItems().size());
        assertEquals(0, element.getCollections().get(1).getCollectionItems().size());

        element.deleteDescendantCollectionItem("anitemid");

        assertEquals(2, element.getCollections().size());
        assertEquals(0, element.getCollections().get(0).getCollectionItems().size());
        assertEquals(0, element.getCollections().get(1).getCollectionItems().size());
    }

    @Test
    public void whenDeleteDescendantCollectionItemAndExists_ThenRemoves() {

        var element = new DraftElement("aname", Map.of(
          "acollectionname1", new DraftElementValue("acollectionname1", Map.of(
            "Id", new DraftElementValue("acollectionid1"),
            "Items", new DraftElementValue(List.of(new DraftElement("aname", Map.of(
              "Id", new DraftElementValue("acollectionitemid1")
            ), false))))),
          "acollectionname2", new DraftElementValue("acollectionname2", Map.of(
            "Id", new DraftElementValue("acollectionid2"),
            "Items", new DraftElementValue(new ArrayList<>() {{
                add(new DraftElement("aname", Map.of(
                  "Id", new DraftElementValue("acollectionitemid2")
                ), false));
            }})
          ))), false);

        assertEquals(2, element.getCollections().size());
        assertEquals(1, element.getCollections().get(0).getCollectionItems().size());
        assertEquals(1, element.getCollections().get(1).getCollectionItems().size());

        element.deleteDescendantCollectionItem("acollectionitemid2");

        assertEquals(2, element.getCollections().size());
        assertEquals(1, element.getCollections().get(0).getCollectionItems().size());
        assertEquals(0, element.getCollections().get(1).getCollectionItems().size());
    }

    @Test
    public void whenIsNotRootAndRoot_ThenReturnsFalse() {

        var element = new DraftElement("aname", new HashMap<>(), true);

        var result = element.isNotRoot();

        assertFalse(result);
    }

    @Test
    public void whenIsNotRootAndNotRoot_ThenReturnsTrue() {

        var element = new DraftElement("aname", new HashMap<>(), false);

        var result = element.isNotRoot();

        assertTrue(result);
    }

    @Test
    public void whenIndexOfWithUnknownElement_ThenReturnsOutOfBounds() {

        var element = new DraftElement("anelementname", Map.of(), false);
        var anotherElement = new DraftElement("anothername", Map.of(), false);

        var result = element.indexOf(anotherElement);

        assertEquals(-1, result);
    }

    @Test
    public void whenIndexOfWithChildElement_ThenReturnsIndex() {

        var element = new DraftElement("anelementname", new LinkedHashMap<>(), false);
        var child1 = new DraftElement("achildname1", Map.of("Id", new DraftElementValue("achildid1")), false);
        var child2 = new DraftElement("achildname2", Map.of("Id", new DraftElementValue("achildid2")), false);
        var child3 = new DraftElement("achildname3", Map.of("Id", new DraftElementValue("achildid3")), false);
        element.addElement(child1);
        element.addElement(child2);
        element.addElement(child3);

        var result = element.indexOf(child2);

        assertEquals(1, result);
    }
}
