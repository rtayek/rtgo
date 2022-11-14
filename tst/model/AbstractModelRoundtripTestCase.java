package model;
import static org.junit.Assert.*;
import java.io.*;
import org.junit.*;
import sgf.*;
import utilities.MyTestWatcher;
public abstract class AbstractModelRoundtripTestCase extends AbstractMNodeRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Ignore @Test public void testMNodeRoundTripTwoEmptyWithSemicolon() throws Exception {
        // these belong by themselves
        MNode root=MNode.restore(new StringReader(expectedSgf));
        // break out/get rid of the model.
        Model model=new Model();
        // move this to model
        model.setRoot(root);
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue(ok);
        String actual=stringWriter.toString();
        actual=actual.replace("\n",""); // who is putting the linefeed in?
        if(!expectedSgf.equals(actual)) ; //printDifferences(expected,actual);
        assertEquals(expectedSgf,actual);
    }
    
}