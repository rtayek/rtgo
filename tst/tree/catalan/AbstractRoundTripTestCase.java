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
        Node<Character> extra=new Node<>(null);
        extra.left=bRoot;
        MNode2<Character> extraMNode2=new MNode2<>(null,null);
        Node.processed.clear();
        mRoot=Node.<Character> from(extra,extraMNode2);
        mRoot.data='.';
        if(verbose) { System.out.println("mRoot"); MNode2.print(mRoot,"",true); }
        MNode2.processed.clear();
        Node<Character> actual=MNode2.oldFrom(mRoot);
        assertNotNull(actual);
        if(verbose) G2.print(actual.left,"   ");
        if(!Node.deepEquals(bRoot,actual.left)) {
            System.out.println("bRoot");
            G2.print(bRoot,"   ");
            System.out.println("actual");
            G2.print(actual.left,"   ");
        }
        assertTrue(key.toString(),Node.deepEquals(bRoot,actual.left));
        // the other way
        System.out.println("---");
        Node<Character> extra2=new Node<>(null);
        extra2.left=bRoot;
        MNode2<Character> extra2MNode2=new MNode2<>(null,null);
        Node.processed.clear();
        mRoot=Node.<Character> from(extra2,extra2MNode2);
        mRoot.data='.';
        MNode2.processed.clear();
        Node<Character> actual2=MNode2.oldFrom(mRoot);
        assertNotNull(actual2);
        if(!Node.deepEquals(actual.left,actual2.left)) {
            G2.print(actual.left,"   ");
            G2.print(actual2.left,"   ");
        }
        assertTrue(key.toString(),Node.deepEquals(actual,actual2));
    }
    boolean verbose=true;
    Object key="";
    Node<Character> bRoot;
    MNode2<Character> mRoot;
}
