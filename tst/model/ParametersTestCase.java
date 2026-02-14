package model;
import utilities.MyTestWatcher;
import static org.junit.Assert.*;
import java.io.File;
import java.util.Properties;
import org.junit.*;
import equipment.Board;
import equipment.Board.Topology;
import com.tayek.util.io.PropertiesIO;
public class ParametersTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    @Ignore @Test public void testInitializeParametersWithDefaults() {
        Integer width=(int)Parameters.width.currentValue();
        assertEquals(Board.standard,width); // just check the width
        // we changed the width parameter to 15 and it was persisted.
        // so this is failing or has a bad name?
        // it's a bad name.
        // 10/23/22 failing now because properties file does not contain the defaults.
    }
    @Test public void testSaveAPropertiesFile() {
        String filename="test.properties";
        File file=new File(filename);
        if(file.exists()) file.delete();
        PropertiesIO.writePropertiesFile(new Properties(),filename);
        assertTrue(new File(filename).exists());
    }
    @Test public void testSaveAndReadAPropertiesFile() {
        Properties expected=new Properties();
        Parameters.setPropertiesFromCurrentValues(expected);
        PropertiesIO.writePropertiesFile(expected,"test.properties");
        Properties actual=new Properties();
        PropertiesIO.loadPropertiesFile(actual,"test.properties");
        assertEquals(expected,actual);
    }
    @Test public void testChange() {
        Parameters.change(Parameters.topology,Topology.torus);
        assertEquals(Topology.torus,Parameters.topology.currentValue());
        Parameters.change(Parameters.topology,Topology.normal); // be sure to change it back!
        assertEquals(Topology.normal,Parameters.topology.currentValue());
        // maybe we need to reset all the parameters before each test.
        // probably a good idea/
    }
    @Ignore @Test public void testWhyDenyIsMissing() { fail("nyi"); }
    @Ignore @Test public void testnewGameFromParameters() {
        // this is potentially a lot of tests
        fail("nyi");
    }
}


