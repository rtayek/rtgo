package sgf;
import static org.junit.Assert.assertEquals;
import org.junit.*;
public abstract class AbstractMNodeRoundTripTestCase extends AbstractSgfRoundTripTestCase {
    @Test public void testMMNodeRoundTrip() throws Exception {
        SgfTestSupport.logBadParentheses(expectedSgf,key,"ex");
        String actualSgf=SgfTestIo.mNodeRoundTrip(expectedSgf,SgfRoundTrip.MNodeSaveMode.standard);
        assertPreparedRoundTripWithParenthesesCheck(actualSgf,"ac");
    }
    @Test public void testMMNodeDirectRoundTrip() throws Exception {
        String actualSgf=SgfTestIo.mNodeRoundTrip(expectedSgf,SgfRoundTrip.MNodeSaveMode.direct);
        //Boolean ok=specialCases(actualSgf);
        //if(ok) return;
        assertPreparedRoundTripWithParenthesesCheck(actualSgf,"ac");
    }
}
