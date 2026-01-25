package tree;
import io.Logging;
import static org.junit.Assert.*;
import static tree.MNode.from;
import static tree.MNode.print;
import static tree.Node.deepEquals;
import static tree.Node.from;
import org.junit.*;
import utilities.TestSupport;
public abstract class AbstractRoundTripTestCase extends TestSupport {
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testNodeRoundTrip() {
        if(bRoot==null) Logging.mainLogger.info("binary tree is null.");
        if(verbose) { Logging.mainLogger.info("bRoot"); G2.print(bRoot,"   "); }
        mRoot=from(bRoot);
        if(verbose) { Logging.mainLogger.info("mRoot"); print(mRoot,"",true); }
        MNode.clearProcessed();
        Node<Long> actual=from(mRoot);
        assertNotNull(actual);
        if(verbose) { Logging.mainLogger.info("actual:"); G2.print(actual.left,"   "); }
        if(!deepEquals(bRoot,actual.left)) {
            Logging.mainLogger.info("bRoot");
            G2.print(bRoot,"   ");
            Logging.mainLogger.info("actual");
            G2.print(actual.left,"   ");
        }
        assertTrue(key.toString(),deepEquals(bRoot,actual.left));
    }
    boolean verbose=false;
    Object key="";
    Node<Long> bRoot;
    MNode<Long> mRoot;
}
