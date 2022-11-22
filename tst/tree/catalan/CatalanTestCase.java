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
    @Parameters public static Collection<Object[]> parameters() { return modulo(max+1); }
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testArrayValueEqualsCalculatedValue() {
        //System.out.println("equal "+(catalans[nodes]==catalan2(nodes)));
        assertEquals(catalans[nodes],catalan2(nodes));
    }
    @Test public void testBotheCalculationsAgree() {
        long c1=catalan(nodes);
        long c2=catalan2(nodes);
        if(c1<0) System.out.println("catalan("+nodes+") fails!");
        if(c2<0) System.out.println("catalan2("+nodes+") fails!");
        if(c1<0||c1<0);
        else assertEquals(catalan(nodes),Catalan.catalan2(nodes));
    }
    // add the other test round trip
    @Test public void testLongRoundTrip() {
        Holder<Integer> data=new Holder<>(0);
        List<Node> trees=g2.all(nodes,data);
        for(Node node:trees) {
            if(node==null) continue; // looks like we need this.
            // this is a round trip
            String expected=Node.encode(node,null);
            // want to pass data to the deocde in here
            String actual=roundTripLong(expected);
            //actual=decodeEncode(expected,data);
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
            // this is a round trip
            String expected=encode(node,null);
            // want to pass data to the deocde in here
            String actual=roundTripLong(expected);
            assertEquals(expected,actual);
        }
    }
    @Test public void testGenerate() {
        Holder<Integer> data=new Holder<>(0);
        List<Node> trees=g2.all(nodes,data);
        //System.out.println("trees "+(catalan(nodes)==trees.size()));
        assertEquals(catalan2(nodes),trees.size());
        // one would think that the first would fail
    }
    int nodes;
    G2 g2=new G2();
    public static final int max=11;
}
