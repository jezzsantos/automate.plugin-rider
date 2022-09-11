package jezzsantos.automate.plugin.application.interfaces.drafts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DraftPropertyTests {

    @Test
    public void whenGetName_ThenReturnsName() {

        var property = new DraftProperty("aname", new DraftElementValue("avalue"));

        var result = property.getName();

        assertEquals("aname", result);
    }

    @Test
    public void whenGetValue_ThenReturnsStringValue() {

        var property = new DraftProperty("aname", new DraftElementValue("avalue"));

        var result = property.getValue();

        assertEquals("avalue", result);
    }

    @SuppressWarnings({"EqualsWithItself", "ConstantConditions"})
    @Test
    public void whenEqualsAndOtherIsThis_ThenReturnsTrue() {

        var property = new DraftProperty("aname", new DraftElementValue("avalue"));

        var result = property.equals(property);

        assertTrue(result);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void whenEqualsAndOtherIsNull_ThenReturnsFalse() {

        var property = new DraftProperty("aname", new DraftElementValue("avalue"));

        var result = property.equals(null);

        assertFalse(result);
    }

    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    @Test
    public void whenEqualsAndOtherIsDifferentType_ThenReturnsFalse() {

        var property = new DraftProperty("aname", new DraftElementValue("avalue"));

        var result = property.equals("anothertype");

        assertFalse(result);
    }

    @Test
    public void whenEqualsAndOtherHasDifferentName_ThenReturnsFalse() {

        var other = new DraftProperty("anotherame", new DraftElementValue("avalue"));
        var property = new DraftProperty("aname", new DraftElementValue("avalue"));

        var result = property.equals(other);

        assertFalse(result);
    }

    @Test
    public void whenEqualsAndOtherHasSameName_ThenReturnsTrue() {

        var other = new DraftProperty("aname", new DraftElementValue("avalue"));
        var property = new DraftProperty("aname", new DraftElementValue("avalue"));

        var result = property.equals(other);

        assertTrue(result);
    }
}
