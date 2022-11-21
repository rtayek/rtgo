package tree.catalan;
import static org.junit.Assert.assertEquals;
import static tree.catalan.Catalan.*;
import static tree.catalan.G2.*;
import static tree.catalan.G2.Node.encode;
import static utilities.ParameterArray.modulo;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import tree.catalan.G2.Node;
import utilities.*;
@RunWith(Parameterized.class) public class CatalanTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    public CatalanTestCase(int nodes) { this.nodes=nodes; }
    //@Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> testData() {
        return modulo(11);
    }
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testArrayValueEqualsCalculatedValue() {
        //System.out.println("equal "+(catalans[nodes]==catalan2(nodes)));
        assertEquals(catalans[nodes],catalan2(nodes));
    }
    @Test public void testBotheCalculationsAgree() {
        //System.out.println(" calc "+(catalan(nodes)==catalan2(nodes)));
        assertEquals(catalan(nodes),Catalan.catalan2(nodes));
    }
    @Test public void testLongRoundTrip() {
        Holder<Integer> data=new Holder<>(0);
        List<Node> trees=g2.all(nodes,data);
        for(Node node:trees) {
            if(node==null) continue;
            // this is just check, needs to be long
            // lets look at check
            String expected=Node.encode(node);
            String actual=roundTrip(expected);
            assertEquals(expected,actual);
        }
    }
    @Test public void testMirrorRoundTrip() { // do we need this?
        // look for duplicate code in node!
        Holder<Integer> data=new Holder<>(0);
        List<Node> trees=g2.all(nodes,data);
        for(Node node:trees) {
            if(node==null) continue;
            mirror(node);
            String expected=encode(node);
            String actual=roundTrip(expected);
            assertEquals(expected,actual);
        }
    }
    @Test public void testMirror() {
        Holder<Integer> data=new Holder<>(0);
        List<Node> trees=g2.all(nodes,data);
        for(Node node:trees) {
            if(node==null) continue;
            String expected=encode(node);
            mirror(node);
            mirror(node);
            String actual=encode(node);
            assertEquals(expected,actual);
        }
    }
    @Test public void testGenerate() {
        Holder<Integer> data=new Holder<>(0);
        List<Node> trees=g2.all(nodes,data);
        //System.out.println("trees "+(catalan(nodes)==trees.size()));
        assertEquals(catalan(nodes),trees.size());
    }
    int nodes;
    G2 g2=new G2();
    public static final int max=15;
}
