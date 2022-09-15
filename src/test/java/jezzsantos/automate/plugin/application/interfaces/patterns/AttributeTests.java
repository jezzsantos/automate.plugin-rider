package jezzsantos.automate.plugin.application.interfaces.patterns;

import jezzsantos.automate.core.AutomateConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AttributeTests {

    @Test
    public void whenToStringWithNoDefaultValueAndNotRequired_ThenReturnsString() {

        var result = new Attribute("anid", "aname")
          .toString();

        assertEquals("aname  (string, optional)", result);
    }

    @Test
    public void whenToStringWithNoDefaultValueAndRequired_ThenReturnsString() {

        var result = new Attribute("anid", "aname", true, null, null, null)
          .toString();

        assertEquals("aname  (string, required)", result);
    }

    @Test
    public void whenToStringWithDefaultValue_ThenReturnsString() {

        var result = new Attribute("anid", "aname", false, "adefaultvalue", null, null)
          .toString();

        assertEquals("aname  (string, optional, default: adefaultvalue)", result);
    }

    @Test
    public void whenToStringWithChoicesAndNoDefaultValue_ThenReturnsString() {

        var result = new Attribute("anid", "aname", false, null, null, List.of("achoice1", "achoice2", "achoice3"))
          .toString();

        assertEquals("aname  (string, optional, oneof: achoice1;achoice2;achoice3)", result);
    }

    @Test
    public void whenToStringWithChoicesAndDefaultValue_ThenReturnsString() {

        var result = new Attribute("anid", "aname", false, "adefaultvalue", null, List.of("achoice1", "achoice2", "achoice3"))
          .toString();

        assertEquals("aname  (string, optional, oneof: achoice1;achoice2;achoice3, default: adefaultvalue)", result);
    }

    @Test
    public void whenIsOneOfChoicesAndValueIsNull_ThenReturnsFalse() {

        var result = new Attribute("anid", "aname", false, null, AutomateConstants.AttributeDataType.DATETIME, new ArrayList<>())
          .isOneOfChoices(null);

        assertFalse(result);
    }

    @Test
    public void whenIsOneOfChoicesAndHasNoChoices_ThenReturnsTrue() {

        var result = new Attribute("anid", "aname", false, null, AutomateConstants.AttributeDataType.DATETIME, new ArrayList<>())
          .isOneOfChoices("achoice");

        assertTrue(result);
    }

    @Test
    public void whenIsOneOfChoicesAndValueNotExist_ThenReturnsFalse() {

        var result = new Attribute("anid", "aname", false, null, AutomateConstants.AttributeDataType.DATETIME, List.of("achoice1", "achoice2", "achoice3"))
          .isOneOfChoices("avalue");

        assertFalse(result);
    }

    @Test
    public void whenIsOneOfChoicesAndValueExists_ThenReturnsTrue() {

        var result = new Attribute("anid", "aname", false, null, AutomateConstants.AttributeDataType.DATETIME, List.of("achoice1", "achoice2", "achoice3"))
          .isOneOfChoices("achoice2");

        assertTrue(result);
    }

    @Nested
    class GivenAStringAttribute {

        private Attribute attribute;

        @BeforeEach
        public void setUp() {

            this.attribute = new Attribute("anid", "aname", AutomateConstants.AttributeDataType.STRING);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsNull_ThenReturnsTrue() {

            var result = this.attribute
              .isValidDataType(null);

            assertTrue(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsString_ThenReturnsTrue() {

            var result = this.attribute
              .isValidDataType("avalue");

            assertTrue(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsBoolean_ThenReturnsTrue() {

            var result = this.attribute
              .isValidDataType("true");

            assertTrue(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsNumber_ThenReturnsTrue() {

            var result = this.attribute
              .isValidDataType("25.5");

            assertTrue(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsDateTime_ThenReturnsTrue() {

            var result = this.attribute
              .isValidDataType("2022-12-01T00:00:00");

            assertTrue(result);
        }
    }

    @Nested
    class GivenAIntegerAttribute {

        private Attribute attribute;

        @BeforeEach
        public void setUp() {

            this.attribute = new Attribute("anid", "aname", AutomateConstants.AttributeDataType.INTEGER);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsNull_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType(null);

            assertFalse(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsString_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType("avalue");

            assertFalse(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsBoolean_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType("true");

            assertFalse(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsInteger_ThenReturnsTrue() {

            var result = this.attribute
              .isValidDataType("25");

            assertTrue(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsFloat_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType("25.5");

            assertFalse(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsDateTime_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType("2022-12-01T00:00:00");

            assertFalse(result);
        }
    }

    @Nested
    class GivenAFloatAttribute {

        private Attribute attribute;

        @BeforeEach
        public void setUp() {

            this.attribute = new Attribute("anid", "aname", AutomateConstants.AttributeDataType.FLOAT);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsNull_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType(null);

            assertFalse(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsString_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType("avalue");

            assertFalse(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsBoolean_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType("true");

            assertFalse(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsInteger_ThenReturnsTrue() {

            var result = this.attribute
              .isValidDataType("25");

            assertTrue(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsFloat_ThenReturnsTrue() {

            var result = this.attribute
              .isValidDataType("25.5");

            assertTrue(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsDateTime_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType("2022-12-01T00:00:00");

            assertFalse(result);
        }
    }

    @Nested
    class GivenABooleanAttribute {

        private Attribute attribute;

        @BeforeEach
        public void setUp() {

            this.attribute = new Attribute("anid", "aname", AutomateConstants.AttributeDataType.BOOLEAN);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsNull_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType(null);

            assertFalse(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsString_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType("avalue");

            assertFalse(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsBoolean_ThenReturnsTrue() {

            var result = this.attribute
              .isValidDataType("true");

            assertTrue(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsInteger_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType("25");

            assertFalse(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsFloat_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType("25.5");

            assertFalse(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsDateTime_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType("2022-12-01T00:00:00");

            assertFalse(result);
        }
    }

    @Nested
    class GivenADateTimeAttribute {

        private Attribute attribute;

        @BeforeEach
        public void setUp() {

            this.attribute = new Attribute("anid", "aname", AutomateConstants.AttributeDataType.DATETIME);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsNull_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType(null);

            assertFalse(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsString_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType("avalue");

            assertFalse(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsBoolean_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType("true");

            assertFalse(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsInteger_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType("25");

            assertFalse(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsFloat_ThenReturnsFalse() {

            var result = this.attribute
              .isValidDataType("25.5");

            assertFalse(result);
        }

        @Test
        public void whenIsValidDataTypeWithStringAndValueIsDateTime_ThenReturnsTrue() {

            var result = this.attribute
              .isValidDataType("2022-12-01T00:00:00Z");

            assertTrue(result);
        }
    }
}
