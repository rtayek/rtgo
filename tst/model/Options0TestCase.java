package model;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import model.OptionsABC.*;
public class Options0TestCase {
    @Test public void testGet() { assertEquals(Frog.fred,option.t); }
    @Test public void testEnums() {
        LinkedHashSet<Enum<?>> expected=new LinkedHashSet<>();
        expected.add(fred);
        assertEquals(expected,options.enums());
    }
    @Test public void testOptions() { assertNotNull(new OptionsABC() {}); }
    @Test public void testValueOf() {
        Enum<?> actual=options.valueOf(fred.name());
        // check for duplicate names?
        // do ww needto use the full name? i.e. Frog.fred instrad of just fred?
        assertEquals(fred,actual);
    }
    @Test public void testsetPropertiesFromCurrentValues() {
        option.setCurrentValue(Integer.valueOf(43));
        Object expected=option.currentValue();
        Properties properties=new Properties();
        options.setPropertiesFromCurrentValues(properties);
        Options0 actual=new Options0();
        actual.setCurrentValuesFromProperties(properties);
        assertEquals(expected,actual.get(fred).currentValue());
    }
    @Test public void testSetCurrentValuesFromProperties() {
        Properties properties=new Properties();
        Integer expected=Integer.valueOf("43");
        properties.setProperty(fred.name(),expected.toString());
        Options0 options=new Options0();
        options.setCurrentValuesFromProperties(properties);
        Object actual=options.get(fred).currentValue();
        assertEquals(expected,actual);
    }
    @Test public void testLoadCurrentValuesFromPropertiesFile() {
        Object expected=option.currentValue();
        options.loadCurrentValuesFromPropertiesFile(propertiesFilename);
        //assertEquals(expected,option.currentValue());
        // does not really test anything
    }
    @Test public void testStoreCurrentValuesInPropertiesFile() {
        // also tests load (above)
        Object expected=Integer.valueOf(43);
        option.setCurrentValue(expected);
        options.storeCurrentValuesInPropertiesFile(propertiesFilename);
        options.loadCurrentValuesFromPropertiesFile(propertiesFilename);
        assertEquals(expected,option.currentValue());
    }
    @Test public void testInitializeParameters() {
        options.initializeParameters(propertiesFilename);
    }
    @Test public void testToString() { String expected="{Frog.fred=42}"; assertEquals(expected,options.toString()); }
    enum E { e } // just for test from string
    @Test public void testFromString() {
        Frog actual=OptionsABC.fromString(Frog.class,fred.name());
        assertEquals(fred,actual);
        actual=OptionsABC.fromString(Frog.class,"foo");
        assertNull(actual);
        E e=OptionsABC.fromString(E.class,fred.name());
        assertNull(e);
        // maybe testing too many things?
    }
    final Options0 options=new Options0();
    final Frog fred=Frog.fred;
    final Option<Frog,Letter> option=options.get(fred);
    static final String propertiesFilename="frog.properties";
    // ype safety: The expression of type OptionsABC.Option
    // needs unchecked conversion to conform to OptionsABC.Option<OptionsABC.Frog>
}
