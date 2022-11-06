package utilities;
import static org.junit.Assert.*;
import static utilities.Utilities.isValidName;
import org.junit.Test;
public class UtilitiesTestCase {
    @Test public void testIsValidName() {
        assertTrue(isValidName("Pair"));
        assertFalse(isValidName("foo.Pair"));
        assertFalse(isValidName("Pair<K,V>"));
    }
}
