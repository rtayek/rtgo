package tree.catalan;
import static org.junit.Assert.*;
import static tree.catalan.Catalan.*;
import static tree.catalan.G2.*;
import static tree.catalan.G2.Node.*;
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
    @Test public void testEncodeEncode() {
        Holder<Integer> dataHolder=new Holder<>(0);
        List<Node> trees=g2.all(nodes,dataHolder);
        for(Node expected:trees) {
            if(expected==null) continue; // looks like we need this.
            String encodedd=encode(expected,null);
            Node acatual=decode(encodedd,null);
            assertTrue(structureDeepEquals(expected,acatual));
        }
    }
    @Test public void testCopy() {
        Holder<Integer> dataHolder=new Holder<>(0);
        List<Node> trees=g2.all(nodes,dataHolder);
        for(Node expected:trees) {
            //if(expected==null) continue; // looks like we need this.
            Node actual=copy(expected);
            assertTrue(structureDeepEquals(expected,actual));
            String expectedEncoded=encode(expected,null);
            String actualEncoded=encode(actual,null);
            assertEquals(expectedEncoded,actualEncoded);
        }
    }
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
        assertEquals(catalan2(nodes),trees.size());
    }
    int nodes;
    G2 g2=new G2();
    public static final int max=7; // 11
}
