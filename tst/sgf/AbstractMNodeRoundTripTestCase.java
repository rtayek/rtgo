package sgf;
import static org.junit.Assert.assertEquals;
import static sgf.MNode.mNodeRoundTrip;
import java.io.*;
import org.junit.*;
import utilities.MyTestWatcher;
public abstract class AbstractMNodeRoundTripTestCase extends AbstractSgfRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testMMNodeoundTrip() throws Exception {
        StringReader stringReader=new StringReader(expectedSgf);
        StringWriter stringWriter=new StringWriter();
        MNode root=mNodeRoundTrip(stringReader,stringWriter);
        String actualSgf=stringWriter.toString();
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        Boolean ok=specialCases(actualSgf);
        if(ok) return;
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
}
