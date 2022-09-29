//package jezzsantos.automate.plugin.application.interfaces.drafts;
//
//import org.junit.jupiter.api.Test;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class ElementMapTests {
//
//    @Test
//    public void whenGetByIndexAndNotExists_ThenThrows() {
//
//        assertThrows(IndexOutOfBoundsException.class, () ->
//          new ElementMap(Map.of())
//            .get(0));
//    }
//
//    @Test
//    public void whenGetByNameAndNotExists_ThenReturnsNull() {
//
//        var result = new ElementMap(Map.of())
//          .get("aname");
//
//        assertNull(result);
//    }
//
//    @Test
//    public void whenGetByIndexAndExists_ThenReturnsInstance() {
//
//        var element = new DraftElement("aname", Map.of(), false);
//
//        var result = new ElementMap(Map.of("aname", element))
//          .get(0);
//
//        assertEquals(element, result);
//    }
//
//    @Test
//    public void whenGetByNameAndExists_ThenReturnsInstance() {
//
//        var element = new DraftElement("aname", Map.of(), false);
//
//        var result = new ElementMap(Map.of("aname", element))
//          .get("aname");
//
//        assertEquals(element, result);
//    }
//
//    @Test
//    public void whenSizeAndEmpty_ThenReturnsZero() {
//
//        var result = new ElementMap(Map.of())
//          .size();
//
//        assertEquals(0, result);
//    }
//
//    @Test
//    public void whenSizeAndHasItems_ThenReturnsSize() {
//
//        var element1 = new DraftElement("aname1", Map.of(), false);
//        var element2 = new DraftElement("aname2", Map.of(), false);
//
//        var result = new ElementMap(Map.of(
//          "aname1", element1,
//          "aname2", element2
//        ))
//          .size();
//
//        assertEquals(2, result);
//    }
//
//    @Test
//    public void whenIsEmptyAndEmpty_ThenReturnsTrue() {
//
//        var result = new ElementMap(Map.of())
//          .isEmpty();
//
//        assertTrue(result);
//    }
//
//    @Test
//    public void whenIsEmptyAndHasItems_ThenReturnsFalse() {
//
//        var element1 = new DraftElement("aname1", Map.of(), false);
//        var element2 = new DraftElement("aname2", Map.of(), false);
//
//        var result = new ElementMap(Map.of(
//          "aname1", element1,
//          "aname2", element2
//        ))
//          .isEmpty();
//
//        assertFalse(result);
//    }
//
//    @Test
//    public void whenForEachAndHasItems_ThenReturnsEnumeratesEachItem() {
//
//        var element1 = new DraftElement("aname1", Map.of(), false);
//        var element2 = new DraftElement("aname2", Map.of(), false);
//
//        var map = new ElementMap(new LinkedHashMap<>() {{
//            put("aname1", element1);
//            put("aname2", element2);
//        }});
//
//        var counter = 0;
//        for (var item : map) {
//            if (counter == 0) {
//                assertEquals(element1, item);
//            }
//            if (counter == 1) {
//                assertEquals(element2, item);
//            }
//
//            counter++;
//        }
//
//        assertEquals(2, counter);
//    }
//}
