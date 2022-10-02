package jezzsantos.automate.plugin.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringWithDefaultTests {

    @Test
    public void whenConstructedWithImplicitValue_ThenInitialized() {

        var string = new StringWithDefault("animplicitvalue");

        assertEquals("animplicitvalue", string.getDefaultValue());
        assertEquals("", string.getValue());
    }

    @Test
    public void whenConstructedWithCurrentValueAndDifferentThanImplicit_ThenInitialized() {

        var string = new StringWithDefault("animplicitvalue", "acurrentvalue");

        assertEquals("animplicitvalue", string.getDefaultValue());
        assertEquals("acurrentvalue", string.getValue());
    }

    @Test
    public void whenConstructedWithCurrentValueAndSameAsImplicit_ThenInitialized() {

        var string = new StringWithDefault("animplicitvalue", "animplicitvalue");

        assertEquals("animplicitvalue", string.getDefaultValue());
        assertEquals("", string.getValue());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void whenEqualsAndOtherIsNull_ThenReturnsFalse() {

        var other = (StringWithDefault) null;

        var first = new StringWithDefault("animplicitvalue");

        var result = first.equals(other);

        assertFalse(result);
    }

    @Test
    public void whenEqualsAndOtherIsAnotherType_ThenReturnsFalse() {

        var other = new Object();

        var first = new StringWithDefault("animplicitvalue");

        var result = first.equals(other);

        assertFalse(result);
    }

    @Test
    public void whenEqualsAndOtherHasDifferentImplicitValueAndSameValue_ThenReturnsFalse() {

        var other = new StringWithDefault("animplicitvalue1");

        var first = new StringWithDefault("animplicitvalue2");

        var result = first.equals(other);

        assertFalse(result);
    }

    @Test
    public void whenEqualsAndOtherHasSameImplicitValueAndDifferentValue_ThenReturnsFalse() {

        var other = new StringWithDefault("animplicitvalue");
        other.setValue("anothervalue");

        var first = new StringWithDefault("animplicitvalue");
        first.setValue("avalue");

        var result = first.equals(other);

        assertFalse(result);
    }

    @Test
    public void whenEqualsAndOtherHasSameImplicitValueAndNoValue_ThenReturnsTrue() {

        var other = new StringWithDefault("animplicitvalue");

        var first = new StringWithDefault("animplicitvalue");

        var result = first.equals(other);

        assertTrue(result);
    }

    @Test
    public void whenEqualsAndOtherHasSameImplicitValueAndSameValue_ThenReturnsTrue() {

        var other = new StringWithDefault("animplicitvalue");
        other.setValue("avalue");

        var first = new StringWithDefault("animplicitvalue");
        first.setValue("avalue");

        var result = first.equals(other);

        assertTrue(result);
    }

    @Test
    public void whenEqualAndImplicitValueAndCurrentValueMatch_ThenReturnsTrue() {

        var other = new StringWithDefault("animplicitvalue");
        other.setValue("animplicitvalue");

        var first = new StringWithDefault("animplicitvalue");

        var result = first.equals(other);

        assertTrue(result);
    }

    @Test
    public void whenSetValueWithNewValue_ThenCurrentValueIsNewValue() {

        var string = new StringWithDefault("animplicitvalue");
        string.setValue("avalue");

        var result = string.getValue();

        assertEquals("avalue", result);
    }

    @Test
    public void whenSetValueWithDefaultValue_ThenCurrentValueIsDefault() {

        var value = new StringWithDefault("animplicitvalue");
        value.setValue("");

        var result = value.getValue();

        assertEquals("", result);
    }

    @Test
    public void whenSetValueWithImplicitValue_ThenCurrentValueIsDefault() {

        var value = new StringWithDefault("animplicitvalue");
        value.setValue("animplicitvalue");

        var result = value.getValue();

        assertEquals("", result);
    }

    @Test
    public void whenIsCustomizedWithNewValue_ThenReturnsTrue() {

        var string = new StringWithDefault("animplicitvalue");
        string.setValue("avalue");

        var result = string.isCustomized();

        assertTrue(result);
    }

    @Test
    public void whenIsCustomizedWithDefaultValue_ThenReturnsFalse() {

        var string = new StringWithDefault("animplicitvalue");
        string.setValue("");

        var result = string.isCustomized();

        assertFalse(result);
    }

    @Test
    public void whenIsCustomizedWithImplicitValue_ThenReturnsFalse() {

        var value = new StringWithDefault("animplicitvalue");
        value.setValue("animplicitvalue");

        var result = value.isCustomized();

        assertFalse(result);
    }

    @Test
    public void whenGetValueOrDefaultAndHasNone_ThenReturnsDefault() {

        var string = new StringWithDefault("animplicitvalue");

        var result = string.getValueOrDefault();

        assertEquals("animplicitvalue", result);
    }

    @Test
    public void whenGetValueOrDefaultAndCustomValue_ThenReturnsDefault() {

        var string = new StringWithDefault("animplicitvalue");
        string.setValue("avalue");

        var result = string.getValueOrDefault();

        assertEquals("avalue", result);
    }

    @Test
    public void whenGetValueOrDefaultAndHasImplicitValue_ThenReturnsDefault() {

        var string = new StringWithDefault("animplicitvalue");
        string.setValue("animplicitvalue");

        var result = string.getValueOrDefault();

        assertEquals("animplicitvalue", result);
    }
}
