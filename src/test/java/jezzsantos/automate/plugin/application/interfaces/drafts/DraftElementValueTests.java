//package jezzsantos.automate.plugin.application.interfaces.drafts;
//
//import org.junit.jupiter.api.Test;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class DraftElementValueTests {
//
//    @Test
//    public void whenConstructedWithString_ThenIsProperty() {
//
//        var result = new DraftElementValue("astringvalue");
//
//        assertTrue(result.isProperty());
//        assertFalse(result.isElement());
//        assertFalse(result.isCollection());
//    }
//
//    @Test
//    public void whenConstructedWithHashMap_ThenIsElement() {
//
//        var result = new DraftElementValue("aname", new HashMap<>());
//
//        assertFalse(result.isProperty());
//        assertTrue(result.isElement());
//        assertFalse(result.isCollection());
//    }
//
//    @Test
//    public void whenConstructedWithListOfElements_ThenHasCollectionItems() {
//
//        var list = List.of(new DraftElement("aname", Map.of(), false));
//
//        var result = new DraftElementValue(list);
//
//        assertFalse(result.isProperty());
//        assertTrue(result.isElement());
//        assertFalse(result.isCollection());
//        assertEquals(1, result.getCollectionItems().size());
//    }
//
//    @Test
//    public void whenGetValueAndProperty_ThenReturnsPropertyValue() {
//
//        var value = new DraftElementValue("astringvalue");
//
//        var result = value.getValue();
//
//        assertEquals("astringvalue", result);
//    }
//
//    @Test
//    public void whenGetValueAndElement_ThenReturnsNull() {
//
//        var value = new DraftElementValue("aname", new HashMap<>());
//
//        var result = value.getValue();
//
//        assertNull(result);
//    }
//
//    @Test
//    public void whenGetValueAndCollectionItems_ThenReturnsNull() {
//
//        var value = new DraftElementValue(new ArrayList<>());
//
//        var result = value.getValue();
//
//        assertNull(result);
//    }
//
//    @Test
//    public void whenGetCollectionAndProperty_ThenReturnsNull() {
//
//        var value = new DraftElementValue("astringvalue");
//
//        var result = value.getCollection();
//
//        assertNull(result);
//    }
//
//    @Test
//    public void whenGetCollectionAndElement_ThenReturnsNull() {
//
//        var value = new DraftElementValue("aname", new HashMap<>());
//
//        var result = value.getCollection();
//
//        assertNull(result);
//    }
//
//    @Test
//    public void whenGetCollectionAndElementAndHasItems_ThenReturnsNull() {
//
//        var map = new HashMap<String, DraftElementValue>();
//        map.put("Items", new DraftElementValue(new ArrayList<>()));
//
//        var value = new DraftElementValue("aname", map);
//
//        var result = value.getCollection();
//
//        assertNotNull(result);
//    }
//
//    @Test
//    public void whenGetCollectionAndHasCollectionItems_ThenReturnsNull() {
//
//        var value = new DraftElementValue(new ArrayList<>());
//
//        var result = value.getCollection();
//
//        assertNull(result);
//    }
//
//    @Test
//    public void whenGetCollectionAndCollection_ThenReturnsCollection() {
//
//        var value = new DraftElementValue("aname", new HashMap<>() {{
//            put("Id", new DraftElementValue("anid"));
//            put("Items", new DraftElementValue(List.of(new DraftElement("anelementname", Map.of(), false))));
//        }});
//
//        var result = value.getCollection();
//
//        assertEquals("anid", Objects.requireNonNull(result).getId());
//    }
//
//    @Test
//    public void whenGetElementAndProperty_ThenReturnsNull() {
//
//        var value = new DraftElementValue("astringvalue");
//
//        var result = value.getElement();
//
//        assertNull(result);
//    }
//
//    @Test
//    public void whenGetElementAndElement_ThenReturnsElement() {
//
//        var value = new DraftElementValue("aname", new HashMap<>() {{
//            put("Id", new DraftElementValue("anid"));
//        }});
//
//        var result = value.getElement();
//
//        assertEquals("anid", Objects.requireNonNull(result).getId());
//    }
//
//    @Test
//    public void whenGetElementAndCollectionItems_ThenReturnsNull() {
//
//        var value = new DraftElementValue(new ArrayList<>());
//
//        var result = value.getElement();
//
//        assertNull(result);
//    }
//
//    @Test
//    public void whenGetCollectionItemsAndProperty_ThenReturnsEmpty() {
//
//        var value = new DraftElementValue("astringvalue");
//
//        var result = value.getCollectionItems();
//
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    public void whenGetCollectionItemsAndElement_ThenReturnsEmpty() {
//
//        var value = new DraftElementValue("aname", new HashMap<>());
//
//        var result = value.getCollectionItems();
//
//        assertTrue(result.isEmpty());
//    }
//
//    @Test
//    public void whenGetCollectionItemsAndCollectionItems_ThenReturnsItems() {
//
//        var list = new ArrayList<DraftElement>();
//        var value = new DraftElementValue(list);
//
//        var result = value.getCollectionItems();
//
//        assertEquals(list, result);
//    }
//
//    @Test
//    public void whenDeleteCollectionItemAndProperty_ThenDoesNothing() {
//
//        var value = new DraftElementValue("astringvalue");
//
//        value.deleteCollectionItem(new DraftElement("aname", Map.of(), false));
//    }
//
//    @Test
//    public void whenDeleteCollectionItemAndElement_ThenDoesNothing() {
//
//        var value = new DraftElementValue("aname", new HashMap<>());
//
//        value.deleteCollectionItem(new DraftElement("aname", Map.of(), false));
//    }
//
//    @Test
//    public void whenDeleteCollectionItemAndCollectionItemsAndNoItems_ThenDoesNothing() {
//
//        var list = new ArrayList<DraftElement>();
//        var value = new DraftElementValue(list);
//
//        value.deleteCollectionItem(new DraftElement("aname", Map.of(), false));
//
//        assertEquals(0, value.getCollectionItems().size());
//    }
//
//    @Test
//    public void whenDeleteCollectionItemAndCollectionItemsAndUnknownItem_ThenDoesNothing() {
//
//        var list = new ArrayList<DraftElement>() {{
//            add(new DraftElement("aname", Map.of(
//              "Id", new DraftElementValue("anid")
//            ), false));
//        }};
//        var value = new DraftElementValue(list);
//
//        value.deleteCollectionItem(new DraftElement("aname", Map.of(
//          "Id", new DraftElementValue("anunknownid")
//        ), false));
//
//        assertEquals(1, value.getCollectionItems().size());
//        assertEquals("anid", value.getCollectionItems().get(0).getId());
//    }
//
//    @Test
//    public void whenDeleteCollectionItemAndCollectionItemsExistingItem_ThenDeletesItem() {
//
//        var list = new ArrayList<DraftElement>() {{
//            add(new DraftElement("aname", Map.of(
//              "Id", new DraftElementValue("anid")
//            ), false));
//        }};
//        var value = new DraftElementValue(list);
//
//        value.deleteCollectionItem(new DraftElement("aname", Map.of(
//          "Id", new DraftElementValue("anid")
//        ), false));
//
//        assertEquals(0, value.getCollectionItems().size());
//    }
//}
