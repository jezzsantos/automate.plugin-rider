//package jezzsantos.automate.plugin.application.interfaces.drafts;
//
//import org.junit.jupiter.api.Test;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class ElementValueMapTests {
//
//    @Test
//    public void whenGetByIndexAndNotExists_ThenThrows() {
//
//        assertThrows(IndexOutOfBoundsException.class, () ->
//          new ElementValueMap(Map.of())
//            .get(0));
//    }
//
//    @Test
//    public void whenGetByNameAndNotExists_ThenReturnsNull() {
//
//        var result = new ElementValueMap(Map.of())
//          .get("aname");
//
//        assertNull(result);
//    }
//
//    @SuppressWarnings("ConstantConditions")
//    @Test
//    public void whenGetByIndexAndExists_ThenReturnsInstance() {
//
//        var value = new DraftElementValue("avalue");
//
//        var result = new ElementValueMap(Map.of("aname", value))
//          .get(0);
//
//        assertEquals("aname", result.getName());
//        assertEquals("avalue", result.getValue());
//    }
//
//    @SuppressWarnings("ConstantConditions")
//    @Test
//    public void whenGetByNameAndExists_ThenReturnsInstance() {
//
//        var value = new DraftElementValue("avalue");
//
//        var result = new ElementValueMap(Map.of("aname", value))
//          .get("aname");
//
//        assertEquals("aname", result.getName());
//        assertEquals("avalue", result.getValue());
//    }
//
//    @Test
//    public void whenSizeAndEmpty_ThenReturnsZero() {
//
//        var result = new ElementValueMap(Map.of())
//          .size();
//
//        assertEquals(0, result);
//    }
//
//    @Test
//    public void whenSizeAndHasItems_ThenReturnsSize() {
//
//        var value1 = new DraftElementValue("avalue1");
//        var value2 = new DraftElementValue("avalue2");
//
//        var result = new ElementValueMap(Map.of(
//          "aname1", value1,
//          "aname2", value2
//        ))
//          .size();
//
//        assertEquals(2, result);
//    }
//
//    @Test
//    public void whenIsEmptyAndEmpty_ThenReturnsTrue() {
//
//        var result = new ElementValueMap(Map.of())
//          .isEmpty();
//
//        assertTrue(result);
//    }
//
//    @Test
//    public void whenIsEmptyAndHasItems_ThenReturnsFalse() {
//
//        var value1 = new DraftElementValue("avalue1");
//        var value2 = new DraftElementValue("avalue2");
//
//        var result = new ElementValueMap(Map.of(
//          "aname1", value1,
//          "aname2", value2
//        ))
//          .isEmpty();
//
//        assertFalse(result);
//    }
//
//    @Test
//    public void whenForEachAndHasItems_ThenReturnsEnumeratesEachItem() {
//
//        var value1 = new DraftElementValue("avalue1");
//        var value2 = new DraftElementValue("avalue2");
//
//        var map = new ElementValueMap(new LinkedHashMap<>() {{
//            put("aname1", value1);
//            put("aname2", value2);
//        }});
//
//        var counter = 0;
//        for (var item : map) {
//            if (counter == 0) {
//                assertEquals("avalue1", item.getValue());
//            }
//            if (counter == 1) {
//                assertEquals("avalue2", item.getValue());
//            }
//
//            counter++;
//        }
//
//        assertEquals(2, counter);
//    }
//}
