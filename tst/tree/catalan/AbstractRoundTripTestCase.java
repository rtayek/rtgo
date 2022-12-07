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
        if(verbose) { System.out.println("bRoot"); G2.print(bRoot,"   "); }
        mRoot=Node.from(bRoot);
        if(verbose) { System.out.println("mRoot"); MNode2.print(mRoot,"",true); }

        MNode2.processed.clear();
        Node<Long> actual=MNode2.oldFrom(mRoot);
        assertNotNull(actual);
        if(verbose) {
            System.out.println("actual:");
            G2.print(actual.left,"   ");}
        if(!Node.deepEquals(bRoot,actual.left)) {
            System.out.println("bRoot");
            G2.print(bRoot,"   ");
            System.out.println("actual");
            G2.print(actual.left,"   ");
        }
        assertTrue(key.toString(),Node.deepEquals(bRoot,actual.left));
    }
    boolean verbose=true;
    Object key="";
    Node<Long> bRoot;
    MNode2<Long> mRoot;
}
