package tree.catalan;
import org.junit.*;
import tree.catalan.G2.Node;
import tree.catalan.RedBean.MNode2;
import utilities.MyTestWatcher;
public class AbstractRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void test() { int x=G2.<Character>from(bRoot); }
    Node<Character> bRoot;
    MNode2<Character> mRoot;
}
