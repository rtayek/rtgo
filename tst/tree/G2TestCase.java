package tree;
import static org.junit.Assert.*;
import static tree.Node.*;
import java.util.*;
import org.junit.*;
import tree.G2.Generator;
import utilities.Iterators.Longs;
import utilities.MyTestWatcher;
public class G2TestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    // round trip?
    // parameterize?
    // so far, none of these tests use a non null value for data.
    @BeforeClass public static void setUpBeforeClass() throws Exception {}
    @Before public void setUp() throws Exception {
        g2=new G2();
        //all=new G2().generate(3);
        verbose=false; //
    }
    @After public void tearDown() throws Exception {}
    @Test public void testEncodeNullFalse() {
        trees=Generator.one(0,iterator,false);
        Node<Long> node=trees.get(0);
        assertNull(node);
        String encoded=encode(node,null);
        assertEquals("0",encoded);
    }
    @Test public void testEncodeNulTruel() {
        trees=Generator.one(0,iterator,true);
        Node<Long> node=trees.get(0);
        assertNull(node);
        String encoded=encode(node,null);
        assertEquals("0",encoded);
    }
    @Test public void testEncode1False() {
        trees=Generator.one(1,iterator,false);
        Node<Long> expected=trees.get(0);
        assertNotNull(expected);
        String encoded=encode(expected,null);
        assertEquals("100",encoded);
    }
    @Test public void testEncode1Truel() {
        trees=Generator.one(1,iterator,true);
        Node<Long> node=trees.get(0);
        assertNotNull(node);
        String encoded=encode(node,null);
        assertEquals("100",encoded);
    }
    @Test public void test0False() {
        trees=Generator.one(0,iterator,false);
        Node<Long> tree=trees.get(0);
        String encoded=encode(tree,null);
        assertEquals("0",encoded);
    }
    @Test public void test0Ttue() {
        trees=Generator.one(0,iterator,true);
        Node<Long> tree=trees.get(0);
        String encoded=encode(tree,null);
        assertEquals("0",encoded);
    }
    @Test public void test1False() {
        trees=Generator.one(1,iterator,false);
        Node<Long> tree=trees.get(0);
        String encoded=encode(tree,null);
        assertEquals("100",encoded);
    }
    @Test public void test1Ttue() {
        trees=Generator.one(1,iterator,true);
        Node<Long> tree=trees.get(0);
        String encoded=encode(tree,null);
        assertEquals("100",encoded);
    }
    @Test public void testDecode100() {
        String expected="100";
        // tree is encoded as null
        // should be "0 maybe?
        Node<Long> tree=decode(expected,null);
        G2.p(tree);
        String actual=encode(tree,null);
        assertEquals(expected,actual);
        Node<Long> actualNode=decode(actual,null);
        G2.p(actualNode);
        assertTrue(structureDeepEquals(tree,actualNode));
    }
    @Test public void test2False() {
        trees=Generator.one(2,iterator,false);
        Node<Long> tree=trees.get(0);
        String encoded=encode(tree,null);
        assertEquals("10100",encoded);
        tree=trees.get(1);
        encoded=encode(tree,null);
        assertEquals("11000",encoded);
    }
    @Test public void test2Ttue() {
        trees=Generator.one(2,iterator,true);
        Node<Long> tree=trees.get(0);
        String encoded=encode(tree,null);
        assertEquals("10100",encoded);
        tree=trees.get(1);
        encoded=encode(tree,null);
        assertEquals("11000",encoded);
    }
    @Test public void testDecode11100() {
        String expected="11000";
        //String expected="10100";
        // tree is encoded as null
        // should be "0 maybe?
        Node<Long> tree=decode(expected,null);
        System.out.println("encoded in node: "+tree.encoded);
        System.out.println("ex: "+expected);
        String actual=encode(tree,null);
        System.out.println("ac: "+actual);
        Node<Long> actualNode=decode(actual,null);
        G2.p(tree);
        G2.p(actualNode);
        assertEquals(expected,actual);
        assertTrue(structureDeepEquals(tree,actualNode));
    }
    @Test public void testMirror2() {
        trees=Generator.one(2,iterator,false);
        for(Node<Long> node:trees) {
            if(node==null) continue;
            String expected=encode(node,null);
            mirror(node);
            mirror(node);
            String actual=encode(node,null);
            assertEquals(expected,actual);
        }
    }
    @Test public void testMirror3() {
        trees=Generator.one(3,iterator,false);
        for(Node<Long> node:trees) {
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
    Generator<Long> generator=new Generator<>(false);
    Iterator<Long> iterator=new Longs();
    ArrayList<Node<Long>> trees;
    //ArrayList<ArrayList<Node<Long>>> all;
}
