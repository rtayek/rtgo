package sgf;
import static org.junit.Assert.assertEquals;
import org.junit.*;
public abstract class AbstractMNodeRoundTripTestCase extends AbstractSgfRoundTripTestCase {
    @Test public void testMMNodeRoundTrip() throws Exception {
        SgfTestSupport.logBadParentheses(expectedSgf,key,"ex");
        String actualSgf=SgfTestIo.mNodeRoundTrip(expectedSgf,SgfRoundTrip.MNodeSaveMode.standard);
        String prepared=prepareActual(actualSgf);
        SgfTestSupport.logBadParentheses(prepared,key,"ac");
        assertPreparedEquals(prepared);
    }
    @Test public void testMMNodeDirectRoundTrip() throws Exception {
        String actualSgf=SgfTestIo.mNodeRoundTrip(expectedSgf,SgfRoundTrip.MNodeSaveMode.direct);
        String prepared=prepareActual(actualSgf);
        //Boolean ok=specialCases(actualSgf);
        //if(ok) return;
        SgfTestSupport.logBadParentheses(prepared,key,"ac");
        assertPreparedEquals(prepared);
    }
}
