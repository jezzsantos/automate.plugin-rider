package jezzsantos.automate.plugin.application.interfaces.drafts;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
