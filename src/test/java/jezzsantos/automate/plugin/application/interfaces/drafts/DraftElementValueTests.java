package jezzsantos.automate.plugin.application.interfaces.drafts;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class DraftElementValueTests {

    @Test
    public void whenConstructedWithString_ThenIsProperty() {

        var result = new DraftElementValue("astringvalue");

        assertTrue(result.isProperty());
        assertFalse(result.isElement());
        assertFalse(result.isCollection());
        assertFalse(result.hasCollectionItems());
    }

    @Test
    public void whenGetValueAndProperty_ThenReturnsPropertyValue() {

        var value = new DraftElementValue("astringvalue");

        var result = value.getValue();

        assertEquals("astringvalue", result);
    }

    @Test
    public void whenGetCollectionAndProperty_ThenReturnsNull() {

        var value = new DraftElementValue("astringvalue");

        var result = value.getCollection();

        assertNull(result);
    }

    @Test
    public void whenGetElementAndProperty_ThenReturnsNull() {

        var value = new DraftElementValue("astringvalue");

        var result = value.getElement();

        assertNull(result);
    }

    @Test
    public void whenGetCollectionItemsAndProperty_ThenReturnsEmpty() {

        var value = new DraftElementValue("astringvalue");

        var result = value.getCollectionItems();

        assertTrue(result.isEmpty());
    }

    @Test
    public void whenConstructedWithHashMap_ThenIsElement() {

        var result = new DraftElementValue("aname", new HashMap<>());

        assertFalse(result.isProperty());
        assertTrue(result.isElement());
        assertFalse(result.isCollection());
        assertFalse(result.hasCollectionItems());
    }

    @Test
    public void whenGetValueAndElement_ThenReturnsNull() {

        var value = new DraftElementValue("aname", new HashMap<>());

        var result = value.getValue();

        assertNull(result);
    }

    @Test
    public void whenGetCollectionAndElement_ThenReturnsNull() {

        var value = new DraftElementValue("aname", new HashMap<>());

        var result = value.getCollection();

        assertNull(result);
    }

    @Test
    public void whenGetCollectionAndElementAndHasItems_ThenReturnsNull() {

        var map = new HashMap<String, DraftElementValue>();
        map.put("Items", new DraftElementValue(new ArrayList<>()));

        var value = new DraftElementValue("aname", map);

        var result = value.getCollection();

        assertNotNull(result);
    }

    @Test
    public void whenGetElementAndElement_ThenReturnsElement() {

        var value = new DraftElementValue("aname", new HashMap<>());

        var result = value.getElement();

        assertNotNull(result);
    }

    @Test
    public void whenGetCollectionItemsAndElement_ThenReturnsEmpty() {

        var value = new DraftElementValue("aname", new HashMap<>());

        var result = value.getCollectionItems();

        assertTrue(result.isEmpty());
    }

    @Test
    public void whenConstructedWithList_ThenIsCollectionItems() {

        var result = new DraftElementValue(new ArrayList<>());

        assertFalse(result.isProperty());
        assertFalse(result.isElement());
        assertFalse(result.isCollection());
        assertTrue(result.hasCollectionItems());
    }

    @Test
    public void whenGetValueAndCollectionItems_ThenReturnsNull() {

        var value = new DraftElementValue(new ArrayList<>());

        var result = value.getValue();

        assertNull(result);
    }

    @Test
    public void whenGetCollectionAndCollectionItems_ThenReturnsNull() {

        var value = new DraftElementValue(new ArrayList<>());

        var result = value.getCollection();

        assertNull(result);
    }

    @Test
    public void whenGetElementAndCollectionItems_ThenReturnsNull() {

        var value = new DraftElementValue(new ArrayList<>());

        var result = value.getElement();

        assertNull(result);
    }

    @Test
    public void whenGetCollectionItemsAndCollectionItems_ThenReturnsItems() {

        var list = new ArrayList<DraftElement>();
        var value = new DraftElementValue(list);

        var result = value.getCollectionItems();

        assertEquals(list, result);
    }
}
