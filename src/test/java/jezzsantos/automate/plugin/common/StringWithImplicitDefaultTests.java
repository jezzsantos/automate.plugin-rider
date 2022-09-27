package jezzsantos.automate.plugin.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringWithImplicitDefaultTests {

    @Test
    public void whenConstructedWithImplicitValue_ThenInitialized() {

        var string = new StringWithImplicitDefault("animplicitvalue");

        assertEquals("animplicitvalue", string.getImplicitValue());
        assertEquals("", string.getValue());
    }

    @Test
    public void whenConstructedWithCurrentValueAndDifferentThanImplicit_ThenInitialized() {

        var string = new StringWithImplicitDefault("animplicitvalue", "acurrentvalue");

        assertEquals("animplicitvalue", string.getImplicitValue());
        assertEquals("acurrentvalue", string.getValue());
    }

    @Test
    public void whenConstructedWithCurrentValueAndSameAsImplicit_ThenInitialized() {

        var string = new StringWithImplicitDefault("animplicitvalue", "animplicitvalue");

        assertEquals("animplicitvalue", string.getImplicitValue());
        assertEquals("", string.getValue());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void whenEqualsAndOtherIsNull_ThenReturnsFalse() {

        var other = (StringWithImplicitDefault) null;

        var first = new StringWithImplicitDefault("animplicitvalue");

        var result = first.equals(other);

        assertFalse(result);
    }

    @Test
    public void whenEqualsAndOtherIsAnotherType_ThenReturnsFalse() {

        var other = new Object();

        var first = new StringWithImplicitDefault("animplicitvalue");

        var result = first.equals(other);

        assertFalse(result);
    }

    @Test
    public void whenEqualsAndOtherHasDifferentImplicitValueAndSameValue_ThenReturnsFalse() {

        var other = new StringWithImplicitDefault("animplicitvalue1");

        var first = new StringWithImplicitDefault("animplicitvalue2");

        var result = first.equals(other);

        assertFalse(result);
    }

    @Test
    public void whenEqualsAndOtherHasSameImplicitValueAndDifferentValue_ThenReturnsFalse() {

        var other = new StringWithImplicitDefault("animplicitvalue");
        other.setValue("anothervalue");

        var first = new StringWithImplicitDefault("animplicitvalue");
        first.setValue("avalue");

        var result = first.equals(other);

        assertFalse(result);
    }

    @Test
    public void whenEqualsAndOtherHasSameImplicitValueAndNoValue_ThenReturnsTrue() {

        var other = new StringWithImplicitDefault("animplicitvalue");

        var first = new StringWithImplicitDefault("animplicitvalue");

        var result = first.equals(other);

        assertTrue(result);
    }

    @Test
    public void whenEqualsAndOtherHasSameImplicitValueAndSameValue_ThenReturnsTrue() {

        var other = new StringWithImplicitDefault("animplicitvalue");
        other.setValue("avalue");

        var first = new StringWithImplicitDefault("animplicitvalue");
        first.setValue("avalue");

        var result = first.equals(other);

        assertTrue(result);
    }

    @Test
    public void whenEqualAndImplicitValueAndCurrentValueMatch_ThenReturnsTrue() {

        var other = new StringWithImplicitDefault("animplicitvalue");
        other.setValue("animplicitvalue");

        var first = new StringWithImplicitDefault("animplicitvalue");

        var result = first.equals(other);

        assertTrue(result);
    }

    @Test
    public void whenSetValueWithNewValue_ThenCurrentValueIsNewValue() {

        var string = new StringWithImplicitDefault("animplicitvalue");
        string.setValue("avalue");

        var result = string.getValue();

        assertEquals("avalue", result);
    }

    @Test
    public void whenSetValueWithDefaultValue_ThenCurrentValueIsDefault() {

        var value = new StringWithImplicitDefault("animplicitvalue");
        value.setValue("");

        var result = value.getValue();

        assertEquals("", result);
    }

    @Test
    public void whenSetValueWithImplicitValue_ThenCurrentValueIsDefault() {

        var value = new StringWithImplicitDefault("animplicitvalue");
        value.setValue("animplicitvalue");

        var result = value.getValue();

        assertEquals("", result);
    }

    @Test
    public void whenIsCustomizedWithNewValue_ThenReturnsTrue() {

        var string = new StringWithImplicitDefault("animplicitvalue");
        string.setValue("avalue");

        var result = string.isCustomized();

        assertTrue(result);
    }

    @Test
    public void whenIsCustomizedWithDefaultValue_ThenReturnsFalse() {

        var string = new StringWithImplicitDefault("animplicitvalue");
        string.setValue("");

        var result = string.isCustomized();

        assertFalse(result);
    }

    @Test
    public void whenIsCustomizedWithImplicitValue_ThenReturnsFalse() {

        var value = new StringWithImplicitDefault("animplicitvalue");
        value.setValue("animplicitvalue");

        var result = value.isCustomized();

        assertFalse(result);
    }

    @Test
    public void whenCreateCopyWithValue_ThenReturnsSameImplicitValue() {

        var string = new StringWithImplicitDefault("animplicitvalue");

        var result = string.createCopyWithValue("anothervalue");

        assertEquals(string.getImplicitValue(), result.getImplicitValue());
        assertEquals("", string.getValue());
        assertEquals("anothervalue", result.getValue());
        assertNotEquals(result, string);
    }

    @Test
    public void whenGetExplicitValueAndHasNone_ThenReturnsDefault() {

        var string = new StringWithImplicitDefault("animplicitvalue");

        var result = string.getExplicitValue();

        assertEquals("animplicitvalue", result);
    }

    @Test
    public void whenGetExplicitValueAndCustomValue_ThenReturnsDefault() {

        var string = new StringWithImplicitDefault("animplicitvalue");
        string.setValue("avalue");

        var result = string.getExplicitValue();

        assertEquals("avalue", result);
    }

    @Test
    public void whenGetExplicitValueAndHasImplicitValue_ThenReturnsDefault() {

        var string = new StringWithImplicitDefault("animplicitvalue");
        string.setValue("animplicitvalue");

        var result = string.getExplicitValue();

        assertEquals("animplicitvalue", result);
    }
}
