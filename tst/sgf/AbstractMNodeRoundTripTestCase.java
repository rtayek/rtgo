package sgf;
import static org.junit.Assert.assertEquals;
import org.junit.*;
public abstract class AbstractMNodeRoundTripTestCase extends AbstractSgfRoundTripTestCase {
    private void assertMNodeRoundTrip(SgfRoundTrip.MNodeSaveMode saveMode,boolean logExpected) {
        SgfRoundTripHarness.assertMNodeRoundTrip(key,expectedSgf,saveMode,logExpected);
    }
    @Test public void testMMNodeRoundTrip() throws Exception {
        assertMNodeRoundTrip(SgfRoundTrip.MNodeSaveMode.standard,true);
    }
    @Test public void testMMNodeDirectRoundTrip() throws Exception {
        assertMNodeRoundTrip(SgfRoundTrip.MNodeSaveMode.direct,false);
    }
}
