package model;
import static org.junit.Assert.*;
import org.junit.Test;
import model.OptionsABC.*;
public class Option0TestCase { // tests for one option.
    @Test public void testOption() { assertNotNull(option); }
    @Test public void testFromString() {
        Integer expected=42;
        assertEquals(expected,option.currentValue());
        // tests that the overwritten from string worked
        // no, it does not!
    }
    @Test public void testReset() {
        Integer expected=42;
        Integer expected2=43;
        option.setCurrentValue(expected2);
        assertEquals(expected2,option.currentValue());
        option.reset();
        assertEquals(expected,option.currentValue());
    }
    @Test public void testSetCurrentValue() {
        Integer expected=43;
        option.setCurrentValue(expected);
        assertEquals(expected,option.currentValue());
    }
    @Test public void testCurrentValue() { assertEquals(option.defaultValue,option.currentValue()); }
    @Test public void testToString() { String ecxpected="fred=42(42)"; assertEquals(ecxpected,option.toString()); }
    Options0 options=new Options0();
    final Frog fred=Frog.fred;
    final Option<Frog,Integer> option=options.get(fred);
}
