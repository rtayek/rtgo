package model;
import static io.IO.noIndent;
import static org.junit.Assert.*;
import java.io.*;
import org.junit.*;
import sgf.*;
import utilities.MyTestWatcher;
public abstract class AbstractModelRoundtripTestCase extends AbstractMNodeRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testModelRoundTrip() throws Exception {
        expectedSgf=expectedSgf.replace("\n","");
        MNode root=MNode.restore(new StringReader(expectedSgf));
        // no model yet!
        StringWriter stringWriter=new StringWriter();
        boolean ok=MNode.save(stringWriter,root,noIndent);
        assertTrue(ok);
        String actual=stringWriter.toString();
        actual=actual.replace("\n",""); // who is putting the linefeed in?
        if(!expectedSgf.equals(actual)) ; //printDifferences(expected,actual);
        assertEquals(expectedSgf,actual);
    }
}