package tree.catalan;
import static org.junit.Assert.*;
import static tree.catalan.G2.mirror;
import static tree.catalan.G2.Node.encode;
import java.util.*;
import org.junit.*;
import tree.catalan.G2.Node;
import utilities.*;
public class G2TestCase {
    // round trip?
    // parameterize?
    // so far, none of these tests use a non null value for data.
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
        String encoded=encode(node,null);
        assertEquals("0",encoded);
    }
    @Test public void testEncodeNulTruel() {
        g2.useMap=true;
        Holder<Integer> data=new Holder<>(0);
        all=g2.generate(0);
        trees=all.get(0);
        Node node=trees.get(0);
        assertNull(node);
        String encoded=encode(node,null);
        assertEquals("0",encoded);
    }
    @Test public void testEncode1False() {
        g2.useMap=false;
        all=g2.generate(1);
        trees=all.get(1);
        Node expected=trees.get(0);
        assertNotNull(expected);
        String encoded=encode(expected,null);
        assertEquals("100",encoded);
    }
    @Test public void testEncode1Truel() {
        g2.useMap=true;
        all=g2.generate(1);
        trees=all.get(1);
        Node node=trees.get(0);
        assertNotNull(node);
        String encoded=encode(node,null);
        assertEquals("100",encoded);
    }
    @Test public void test0False() {
        g2.useMap=false;
        all=g2.generate(0);
        trees=all.get(0);
        Node tree=trees.get(0);
        String encoded=encode(tree,null);
        assertEquals("0",encoded);
    }
    @Test public void test0Ttue() {
        g2.useMap=true;
        all=g2.generate(0);
        trees=all.get(0);
        Node tree=trees.get(0);
        String encoded=encode(tree,null);
        assertEquals("0",encoded);
    }
    @Test public void test1False() {
        g2.useMap=false;
        all=g2.generate(1);
        trees=all.get(1);
        Node tree=trees.get(0);
        String encoded=encode(tree,null);
        assertEquals("100",encoded);
    }
    @Test public void test1Ttue() {
        // duplicated code! fix.
        g2.useMap=true;
        all=g2.generate(1);
        trees=all.get(1);
        Node tree=trees.get(0);
        String encoded=encode(tree,null);
        assertEquals("100",encoded);
    }
    @Test public void test2False() {
        g2.useMap=false;
        all=g2.generate(2);
        trees=all.get(2);
        Node tree=trees.get(0);
        String encoded=encode(tree,null);
        assertEquals("10100",encoded);
        tree=trees.get(1);
        encoded=encode(tree,null);
        assertEquals("11000",encoded);
    }
    @Test public void test2Ttue() {
        g2.useMap=true;
        all=g2.generate(2);
        trees=all.get(2);
        Node tree=trees.get(0);
        String encoded=encode(tree,null);
        assertEquals("10100",encoded);
        tree=trees.get(1);
        encoded=encode(tree,null);
        assertEquals("11000",encoded);
    }
    @Test public void testMirror2() {
        Holder<Integer> data=new Holder<>(0);
        List<Node> trees=g2.all(2,data);
        for(Node node:trees) {
            if(node==null) continue;
            String expected=encode(node,null);
            mirror(node);
            mirror(node);
            String actual=encode(node,null);
            assertEquals(expected,actual);
        }
    }
    @Test public void testMirror3() {
        Holder<Integer> data=new Holder<>(0);
        List<Node> trees=g2.all(3,data);
        for(Node node:trees) {
            if(node==null) continue;
            String expected=encode(node,null);
            mirror(node);
            mirror(node);
            String actual=encode(node,null);
            assertEquals(expected,actual);
        }
    }
    
    G2 g2;
    boolean verbose;
    ArrayList<Node> trees;
    ArrayList<ArrayList<Node>> all;
}
