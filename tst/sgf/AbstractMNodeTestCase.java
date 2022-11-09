package sgf;
import static org.junit.Assert.assertEquals;
import java.io.*;
import org.junit.*;
import utilities.MyTestWatcher;
public abstract class AbstractMNodeTestCase extends AbstractParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    // move mnode test cases here
    @Test public void testLongRoundTrip() throws Exception {
        StringWriter stringWriter=new StringWriter();
        MNode games=MNode.mNodeRoundTrip(expectedSgf!=null?new StringReader(expectedSgf):null,stringWriter);
        String actualSgf=expectedSgf!=null?stringWriter.toString():null;
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testLongRoundTrip2() throws Exception {
        StringWriter stringWriter=new StringWriter();
        @SuppressWarnings("unused") MNode games=MNode.mNoderoundTrip2(expectedSgf,stringWriter);
        String actualSgf=stringWriter.toString();
        if(expectedSgf==null) actualSgf=null; // hack for now
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
}
