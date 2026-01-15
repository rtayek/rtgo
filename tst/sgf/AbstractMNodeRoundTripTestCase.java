package sgf;
import static org.junit.Assert.assertEquals;
import org.junit.*;
public abstract class AbstractMNodeRoundTripTestCase extends AbstractSgfRoundTripTestCase {
    private void assertMNodeRoundTrip(SgfRoundTrip.MNodeSaveMode saveMode,boolean logExpected) {
        if(logExpected) SgfTestSupport.logBadParentheses(expectedSgf,key,"ex");
        String actualSgf=SgfTestIo.mNodeRoundTrip(expectedSgf,saveMode);
        assertPreparedRoundTripWithParenthesesCheck(actualSgf,"ac");
    }
    @Test public void testMMNodeRoundTrip() throws Exception {
        assertMNodeRoundTrip(SgfRoundTrip.MNodeSaveMode.standard,true);
    }
    @Test public void testMMNodeDirectRoundTrip() throws Exception {
        assertMNodeRoundTrip(SgfRoundTrip.MNodeSaveMode.direct,false);
    }
}
