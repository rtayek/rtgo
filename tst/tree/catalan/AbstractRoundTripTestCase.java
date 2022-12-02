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
        Node.processed.clear();
        mRoot=Node.<Character> from(bRoot);
        if(mRoot==null) System.out.println("general tree is null.");
        MNode2.processed.clear();
        Node<Character> actual=MNode2.oldFrom(mRoot);
        assertNotNull(actual);
        if(actual!=null) actual=actual.left;
        if(actual!=null)
            System.out.println(Node.encode(bRoot,null)+" "+Node.encode(actual,null));
        assertTrue(Node.deepEquals(bRoot,actual));
    }
    Node<Character> bRoot;
    MNode2<Character> mRoot;
}
