package tree.catalan;
import static org.junit.Assert.*;
import static tree.catalan.Catalan.*;
import static tree.catalan.G2.roundTripLong;
import static tree.catalan.G2.Node.*;
import static utilities.ParameterArray.modulo;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import tree.catalan.G2.*;
import utilities.MyTestWatcher;
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
        if(c1<0||c1<0); // check for overflow
        else assertEquals(catalan(nodes),Catalan.catalan2(nodes));
    }
    @Test public void testEncodeEncode() {
        ArrayList<Node<Integer>> trees=Generator.<Integer> one(nodes,iterator,false);
        for(Node<Integer> expected:trees) {
            String encodedd=encode(expected,null);
            Node<Integer> acatual=decode(encodedd,null);
            assertTrue(structureDeepEquals(expected,acatual));
        }
    }
    @Test public void testCopy() {
        ArrayList<Node<Integer>> trees=Generator.one(nodes,iterator,false);
        for(Node<Integer> expected:trees) {
            //if(expected==null) continue; // looks like we need this.
            Node<Integer> actual=copy(expected);
            assertTrue(structureDeepEquals(expected,actual));
            ArrayList<Integer> data2=new ArrayList<>();
            String expectedEncoded=encode(expected,data2);
            String actualEncoded=encode(actual,data2);
            assertEquals(expectedEncoded,actualEncoded);
            assertTrue(deepEquals(expected,actual));
        }
    }
    @Test public void testRelabel() {
        ArrayList<Node<Integer>> trees=Generator.one(nodes,iterator,false);
        for(Node<Integer> expected:trees) {
            G2.print(expected,"");
            Iterator<Character> j=new G2.Characters();
            Node<Character> actual=Node.reLabel(expected,j);
            G2.print(actual,"");
            Iterator<Integer> i=new G2.Integers();
            Node<Integer> actual2=Node.reLabel(actual,i);
            G2.print(actual2,"");
            // fails because ?
            assertTrue(structureDeepEquals(expected,actual2));
            assertTrue(deepEquals(expected,actual2));
        }
    }
    @Test public void testCheck() {
        ArrayList<Node<Integer>> trees=Generator.one(nodes,iterator,false);
        for(Node<Integer> node:trees) {
            int n=check(node);
            assertEquals(0,n);
        }
    }
    @Test public void testLongRoundTrip() {
        ArrayList<Node<Integer>> trees=Generator.one(nodes,iterator,false);
        for(Node<Integer> node:trees) {
            // if(node==null) continue; // looks like we need this.
            // this is a round trip
            String expected=Node.encode(node,null);
            // want to pass data to the deocde in here
            String actual=roundTripLong(expected);
            assertEquals(expected,actual);
        }
    }
    @Test public void testMirrorRoundTrip() { // do we need this?
        // look for duplicate code in node!
        ArrayList<Node<Integer>> trees=Generator.one(nodes,iterator,false);
        for(Node<Integer> node:trees) {
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
        ArrayList<Node<Integer>> trees=Generator.one(nodes,iterator,false);
        assertEquals(catalan2(nodes),trees.size());
    }
    int nodes;
    Iterator<Integer> iterator=new G2.Integers();
    public static final int max=7; // 11
}
