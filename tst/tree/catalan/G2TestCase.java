package tree.catalan;
import static org.junit.Assert.*;
import static tree.catalan.G2.mirror;
import static tree.catalan.G2.Node.*;
import java.util.*;
import org.junit.*;
import tree.catalan.G2.Node;
import utilities.*;
public class G2TestCase {
    // round trip?
    // parameterize?
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @BeforeClass public static void setUpBeforeClass() throws Exception {

    }
    @Before public void setUp() throws Exception {
        g2=new G2();
        //all=new G2().generate(3);
        verbose=false; //
    }
    @After public void tearDown() throws Exception {}
    @Test public void testEncodeNullFalse() {
        Holder<Integer> data=new Holder<>(0);
        //ArrayList<Node> trees=allBinaryTrees(0,data);
        all=g2.generate(0);
        trees=all.get(0);
        Node node=trees.get(0);
        assertNull(node);
        String encoded=encode(node);
        assertEquals("0",encoded);
        Node node2=decode(encoded,G2.sequentialData);
        assertNull(node2);
    }
    @Test public void testEncodeNulTruel() {
        g2.useMap=true;
        Holder<Integer> data=new Holder<>(0);
        all=g2.generate(0);
        trees=all.get(0);
        Node node=trees.get(0);
        assertNull(node);
        String encoded=encode(node);
        assertEquals("0",encoded);
        Node node2=decode(encoded,G2.sequentialData);
        assertNull(node2);
    }
    @Test public void testEncode1False() {
        g2.useMap=false;
        all=g2.generate(1);
        trees=all.get(1);
        Node node=trees.get(0);
        assertNotNull(node);
        String encoded=encode(node);
        assertEquals("100",encoded);
        Node node2=decode(encoded,G2.sequentialData);
        assertNotNull(node2);
        assertTrue(node.structureDeepEquals(node2));
    }
    @Test public void testEncode1Truel() {
        g2.useMap=true;
        all=g2.generate(1);
        trees=all.get(1);
        Node node=trees.get(0);
        assertNotNull(node);
        String encoded=encode(node);
        assertEquals("100",encoded);
        Node node2=decode(encoded,G2.sequentialData);
        assertNotNull(node2);
        assertTrue(node.structureDeepEquals(node2));
    }
    @Test public void test0False() {
        g2.useMap=false;
        all=g2.generate(0);
        trees=all.get(0);
        //print(trees);
        assertTrue(trees!=null&&trees.size()==1&&trees.get(0)==null);
        //Set<Integer> keys=map2.keySet();
        //System.out.println("keyset: "+keys);
        //assertTrue(keys!=null&&keys.size()==0);
        // fails unless we clear the maps in setup
        // instead of in static setup due to test order.
        // so size of keyset is a bad test.
        // we could do this in another test case?
    }
    @Test public void test0Ttue() {
        g2.useMap=true;
        all=g2.generate(0);
        trees=all.get(0);
        //print(trees);
        assertTrue(trees!=null&&trees.size()==1&&trees.get(0)==null);
        //Set<Integer> keys=map2.keySet();
        //System.out.println("keyset: "+keys);
        //assertTrue(keys!=null&&keys.size()==0);
    }
    @Test public void test1False() {
        g2.useMap=false;
        all=g2.generate(1);
        trees=all.get(1);
        //print(trees);
        assertTrue(trees!=null&&trees.size()==1&&trees.get(0)!=null);
        //Set<Integer> keys=map.keySet();
        //System.out.println("keyset: "+keys);
        //assertTrue(keys!=null&&keys.size()==1);
    }
    @Test public void test1Ttue() {
        g2.useMap=true;
        all=g2.generate(1);
        System.out.println(all);
        trees=all.get(1);
        System.out.println(trees.get(0));
        System.out.println(encode(trees.get(0)));
        //print(trees);
        assertTrue(trees!=null&&trees.size()==1&&trees.get(0)!=null);
        //Set<Integer> keys=map2.keySet();
        //System.out.println("keyset: "+keys);
        //assertTrue(keys!=null&&keys.size()==1);
    }
    @Test public void test2False() {
        g2.useMap=false;
        all=g2.generate(2);
        trees=all.get(2);
        //print(trees);
        assertTrue(trees!=null&&trees.size()==2&&trees.get(0)!=null);
        //Set<Integer> keys=map.keySet();
        //System.out.println("keyset: "+keys);
        //assertTrue(keys!=null&&keys.size()==2);
    }
    @Test public void test2Ttue() {
        g2.useMap=true;
        all=g2.generate(2);
        trees=all.get(2);
        //print(trees);
        assertTrue(trees!=null&&trees.size()==2&&trees.get(0)!=null);
        //Set<Integer> keys=map2.keySet();
        //System.out.println("keyset: "+keys);
        //assertTrue(keys!=null&&keys.size()==2);
    }
    @Test public void testMirror2() {
        Holder<Integer> data=new Holder<>(0);
        List<Node> trees=g2.all(2,data);
        for(Node node:trees) {
            if(node==null) continue;
            String expected=encode(node);
            mirror(node);
            mirror(node);
            String actual=encode(node);
            assertEquals(expected,actual);
        }
    }
    @Test public void testMirror3() {
        Holder<Integer> data=new Holder<>(0);
        List<Node> trees=g2.all(3,data);
        for(Node node:trees) {
            if(node==null) continue;
            String expected=encode(node);
            mirror(node);
            mirror(node);
            String actual=encode(node);
            assertEquals(expected,actual);
        }
    }

    G2 g2;
    boolean verbose;
    ArrayList<Node> trees;
    ArrayList<ArrayList<Node>> all;
}
