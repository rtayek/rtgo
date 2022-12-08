package tree.catalan.old;
import static org.junit.Assert.*;
import static tree.catalan.old.Node.*;
import static utilities.ParameterArray.modulo;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.*;
@Ignore @RunWith(Parameterized.class) public class NodeTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    public NodeTestCase(int nodes) { this.nodes=nodes; watcher.key=new String(nodes+" nodes."); }
    @Parameters public static Collection<Object[]> testData() { return modulo(maxNodesToTest); }
    // tests binary node which is not really used.
    // we need these for when the binary nodes are really sgf nodes.
    // but it seems hard to subclass binary node to get an sgf node
    // yes, that's why we added a data variable to the node.
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testPreOrder() {
        Holder<Integer> data=new Holder<>(0);
        List<Node> trees=allBinaryTrees(nodes,data);
        boolean anyFailures=false;
        for(Node node:trees) {
            if(node==null) continue;
            if(nodes<3) preOrder(node);
            //String expected=node.toBinaryString();
            //String actual=Node.roundTrip(expected);
            //if(!expected.equals(actual)) anyFailures=true;
            //assertEquals(expected,actual);
            // no test! - fix this
        }
        assertTrue(!anyFailures);
    }
    @Test public void testRoundTripEncodeDecodeBinaryTree() {
        Holder<Integer> data=new Holder<>(0);
        List<Node> trees=allBinaryTrees(nodes,data);
        boolean anyFailures=false;
        for(Node node:trees) {
            if(node==null) continue;
            String expected=node.encode();
            String actual=longDecodeEncode(expected);
            if(!expected.equals(actual)) anyFailures=true;
        }
        assertTrue(!anyFailures);
    }
    @Test public void testRoundTripEncodeDecodeBinaryTreeUnravelled() {
        Holder<Integer> data=new Holder<>(0);
        List<Node> trees=allBinaryTrees(nodes,data);
        boolean anyFailures=false;
        boolean anyDeepailures=false;
        for(Node node:trees) {
            if(node==null) continue;
            String expected=node.encode();
            int number=Integer.parseInt(expected,2);
            List<Boolean> list=bits(number,expected.length());
            List<Integer> datas=new ArrayList<>(Node.sequentialData);
            Node node2=decode(list,datas);
            String actual=node2.encode();
            assertEquals(expected,actual);
            System.out.println(nodes+" nodes.");
            boolean structureDeepEquals=node.structureDeepEquals(node2);
            if(!structureDeepEquals) {
                System.out.println("structures are not equal!");
                System.out.println("ex: "+expected);
                System.out.println("ac: "+actual);
                List<String> report=report(node);
                List<String> report2=report(node2);
                System.out.println(report);
                System.out.println(report2);
            } else System.out.println("structures are equal.");
            if(structureDeepEquals) if(!node.deepEquals(node2)) {
                System.out.println("nodes not equal!");
                //print(node);
                //print(node2);
                //System.out.println(toLongString(node));
                //System.out.println(toLongString(node2));
                //System.out.println(node.encode());
                //System.out.println(node2.encode());
                node.fix(node2);
                System.out.println("try to fix.");
                List<String> report=report(node);
                List<String> report2=report(node2);
                System.out.println(report);
                System.out.println(report2);
                if(!node.deepEquals(node2)) {
                    System.out.println("fix failes!");
                    anyDeepailures=true;
                } else System.out.println("fix succeeded!");
            }
            //assertTrue(node.deepEquals(node2));
            if(!expected.equals(actual)) anyFailures=true;
            //assertEquals(expected,actual);
        }
        assertTrue(!anyDeepailures);
        assertTrue(!anyFailures);
    }
    @Test public void testRoundTripToAndFromMultiwayTree() {
        Holder<Integer> data=new Holder<>(0);
        List<Node> trees=Node.allBinaryTrees(nodes,data);
        for(Node node:trees) {
            if(node==null) continue;
            String expected=node.encode();
            Node newTree=roundTrip(node);
            String actual=newTree.encode();
            assertEquals(expected,actual);
        }
    }
    int nodes;
    static final int maxNodesToTest=11; // was 11
}
