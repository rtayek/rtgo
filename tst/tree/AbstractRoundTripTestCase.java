package tree;
import static org.junit.Assert.*;
import static tree.MNode.from;
import static tree.MNode.print;
import static tree.MNode.processed;
import static tree.Node.deepEquals;
import static tree.Node.from;
import org.junit.*;
import utilities.MyTestWatcher;
public abstract class AbstractRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testNodeRoundTrip() {
        if(bRoot==null) System.out.println("binary tree is null.");
        if(verbose) { System.out.println("bRoot"); G2.print(bRoot,"   "); }
        mRoot=from(bRoot);
        if(verbose) { System.out.println("mRoot"); print(mRoot,"",true); }
        processed.clear();
        Node<Long> actual=from(mRoot);
        assertNotNull(actual);
        if(verbose) { System.out.println("actual:"); G2.print(actual.left,"   "); }
        if(!deepEquals(bRoot,actual.left)) {
            System.out.println("bRoot");
            G2.print(bRoot,"   ");
            System.out.println("actual");
            G2.print(actual.left,"   ");
        }
        assertTrue(key.toString(),deepEquals(bRoot,actual.left));
    }
    boolean verbose=false;
    Object key="";
    Node<Long> bRoot;
    MNode<Long> mRoot;
}
