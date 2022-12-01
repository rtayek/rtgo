package tree.catalan;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import org.junit.*;
import tree.catalan.G2.Node;
import utilities.MyTestWatcher;
public class RoundTripTestCase extends AbstractRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    //ArrayList<Node<Character>> trees=new ArrayList<>();
    @Override @Before public void setUp() throws Exception {
        
    }
    @Override @After public void tearDown() throws Exception {}
    @Override @Test public void test() { fail("Not yet implemented"); }
    G2 g2=new G2();
    ArrayList<ArrayList<Node<Integer>>> all=g2.generate(0);

    bRoot=all.get(0);
}
