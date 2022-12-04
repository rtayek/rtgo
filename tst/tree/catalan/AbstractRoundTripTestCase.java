package tree.catalan;
import static org.junit.Assert.*;
import org.junit.*;
import tree.catalan.G2.Node;
import tree.catalan.RedBean.MNode2;
import utilities.MyTestWatcher;
public abstract class AbstractRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testNodeRoundTrip() {
        if(bRoot==null) System.out.println("binary tree is null.");
        Node<Character> extra=new Node<>(null);
        extra.left=bRoot;
        Node.processed.clear();
        mRoot=Node.<Character> from(extra);
        mRoot.data='.';
        MNode2.processed.clear();
        Node<Character> actual=MNode2.oldFrom(mRoot);
        assertNotNull(actual);
        actual=actual.left;
        assertTrue(Node.deepEquals(bRoot,actual));
        // the other way
    }
    Node<Character> bRoot;
    MNode2<Character> mRoot;
}
