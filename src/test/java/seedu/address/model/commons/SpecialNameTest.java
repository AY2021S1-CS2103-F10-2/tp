package seedu.address.model.commons;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class SpecialNameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new SpecialName(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new SpecialName(invalidName));
    }

    @Test
    public void isValidName() {

        // boundary case: null name
        assertThrows(NullPointerException.class, () -> SpecialName.isValidName(null));

        // invalid name
        assertFalse(SpecialName.isValidName("")); // empty string

        // valid names
        assertTrue(SpecialName.isValidName("peter jack")); // alphabets only
        assertTrue(SpecialName.isValidName("12345")); // numbers only
        assertTrue(SpecialName.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(SpecialName.isValidName("Capital Tan")); // with capital letters
        assertTrue(SpecialName.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
        assertTrue(SpecialName.isValidName("~!@#$%^&*()_+{}[];'?><./,")); // names with special symbols

    }
}
