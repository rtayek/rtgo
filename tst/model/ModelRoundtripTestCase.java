package model;
import static io.IO.noIndent;
import static org.junit.Assert.*;
import java.io.*;
import org.junit.*;
import sgf.*;
import utilities.MyTestWatcher;
public abstract class ModelRoundtripTestCase extends MNodeRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testModelRoundTrip() throws Exception {
        expected=expected.replace("\n","");
        MNode root=MNode.restore(new StringReader(expected));

        StringWriter stringWriter=new StringWriter();
        boolean ok=MNode.save(stringWriter,root,noIndent);
        assertTrue(ok);
        String actual=stringWriter.toString();
        actual=actual.replace("\n",""); // who is putting the linefeed in?
        if(!expected.equals(actual)) ; //printDifferences(expected,actual);
        assertEquals(expected,actual);
    }
}