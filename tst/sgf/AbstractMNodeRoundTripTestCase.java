package sgf;
import io.Logging;
import static org.junit.Assert.assertEquals;
import org.junit.*;
import utilities.MyTestWatcher;
public abstract class AbstractMNodeRoundTripTestCase extends AbstractSgfRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testMMNodeRoundTrip() throws Exception {
        int p=Parser.parentheses(expectedSgf);
        if(p!=0) Logging.mainLogger.info("ex bad parentheses: "+p);
        String actualSgf=SgfTestIo.mNodeRoundTrip(expectedSgf,SgfRoundTrip.MNodeSaveMode.standard);
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        /*int*/ p=Parser.parentheses(actualSgf);
        if(p!=0) Logging.mainLogger.info("ac bad parentheses: "+p);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testMMNodeDirectRoundTrip() throws Exception {
        String actualSgf=SgfTestIo.mNodeRoundTrip(expectedSgf,SgfRoundTrip.MNodeSaveMode.direct);
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        //Boolean ok=specialCases(actualSgf);
        //if(ok) return;
        int p=Parser.parentheses(actualSgf);
        if(p!=0) Logging.mainLogger.info(" bad parentheses: "+p);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
}
