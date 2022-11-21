package tree.catalan.old;
import static org.junit.Assert.*;
import static tree.catalan.old.Node.*;
import java.util.ArrayList;
import org.junit.*;
import tree.catalan.old.Node;
import utilities.*;
public class ATestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @BeforeClass public static void setUpBeforeClass() throws Exception {
        MyTestWatcher.defaultVerbosity=true; //
        Node.map.clear();
        Node.map2.clear();
    }
    @Before public void setUp() throws Exception {
        verbose=false; //
        //Node.map.clear();
        //Node.map2.clear();
    }
    @After public void tearDown() throws Exception {}
    @Test public void testEncodeNullFalse() {
        Node.usingMap2=false;
        Holder<Integer> data=new Holder<>(0);
        ArrayList<Node> trees=allBinaryTrees(0,data);
        Node node=trees.get(0);
        assertNull(node);
        String encoded=encode(node);
        assertEquals("0",encoded);
        Node node2=decode(encoded,sequentialData);
        assertNull(node2);
    }
    @Test public void testEncodeNulTruel() {
        Node.usingMap2=true;
        Holder<Integer> data=new Holder<>(0);
        ArrayList<Node> trees=allBinaryTrees(0,data);
        Node node=trees.get(0);
        assertNull(node);
        String encoded=encode(node);
        assertEquals("0",encoded);
        Node node2=decode(encoded,sequentialData);
        assertNull(node2);
    }
    @Test public void testEncode1False() {
        Node.usingMap2=false;
        Holder<Integer> data=new Holder<>(0);
        ArrayList<Node> trees=allBinaryTrees(1,data);
        Node node=trees.get(0);
        assertNotNull(node);
        String encoded=encode(node);
        assertEquals("100",encoded);
        Node node2=decode(encoded,sequentialData);
        assertNotNull(node2);
        assertTrue(node.structureDeepEquals(node2));
    }
    @Test public void testEncode1Truel() {
        Node.usingMap2=true;
        Holder<Integer> data=new Holder<>(0);
        ArrayList<Node> trees=allBinaryTrees(1,data);
        Node node=trees.get(0);
        assertNotNull(node);
        String encoded=encode(node);
        assertEquals("100",encoded);
        Node node2=decode(encoded,sequentialData);
        assertNotNull(node2);
        assertTrue(node.structureDeepEquals(node2));
    }
    @Test public void test0False() {
        Node.usingMap2=false;
        Holder<Integer> data=new Holder<>(0);
        ArrayList<Node> trees=allBinaryTrees(0,data);
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
        Node.usingMap2=true;
        Holder<Integer> data=new Holder<>(0);
        ArrayList<Node> trees=allBinaryTrees(0,data);
        //print(trees);
        assertTrue(trees!=null&&trees.size()==1&&trees.get(0)==null);
        //Set<Integer> keys=map2.keySet();
        //System.out.println("keyset: "+keys);
        //assertTrue(keys!=null&&keys.size()==0);
    }
    @Test public void test1False() {
        Node.usingMap2=false;
        Holder<Integer> data=new Holder<>(0);
        ArrayList<Node> trees=allBinaryTrees(1,data);
        //print(trees);
        assertTrue(trees!=null&&trees.size()==1&&trees.get(0)!=null);
        //Set<Integer> keys=map.keySet();
        //System.out.println("keyset: "+keys);
        //assertTrue(keys!=null&&keys.size()==1);
    }
    @Test public void test1Ttue() {
        Node.usingMap2=true;
        Holder<Integer> data=new Holder<>(0);
        ArrayList<Node> trees=allBinaryTrees(1,data);
        //print(trees);
        assertTrue(trees!=null&&trees.size()==1&&trees.get(0)!=null);
        //Set<Integer> keys=map2.keySet();
        //System.out.println("keyset: "+keys);
        //assertTrue(keys!=null&&keys.size()==1);
    }
    @Test public void test2False() {
        Node.usingMap2=false;
        Holder<Integer> data=new Holder<>(0);
        ArrayList<Node> trees=allBinaryTrees(2,data);
        print(trees);
        assertTrue(trees!=null&&trees.size()==2&&trees.get(0)!=null);
        //Set<Integer> keys=map.keySet();
        //System.out.println("keyset: "+keys);
        //assertTrue(keys!=null&&keys.size()==2);
    }
    @Test public void test2Ttue() {
        Node.usingMap2=true;
        Holder<Integer> data=new Holder<>(0);
        ArrayList<Node> trees=allBinaryTrees(2,data);
        //print(trees);
        assertTrue(trees!=null&&trees.size()==2&&trees.get(0)!=null);
        //Set<Integer> keys=map2.keySet();
        //System.out.println("keyset: "+keys);
        //assertTrue(keys!=null&&keys.size()==2);
    }
}
