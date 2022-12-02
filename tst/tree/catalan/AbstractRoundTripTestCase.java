package tree.catalan;
import static org.junit.Assert.fail;
import org.junit.*;
import tree.catalan.G2.*;
import tree.catalan.RedBean.MNode2;
import utilities.MyTestWatcher;
public class AbstractRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void test() {
        fail("nyi");
        //mRoot=G2.<Character>from(bRoot);
    }
    G2.Generator<Character> generator=new Generator<>(false);
    Node<Character> bRoot;
    MNode2<Character> mRoot;
}
