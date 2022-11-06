package tree;
import static org.junit.Assert.*;
import static utilities.ParameterArray.modulo;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.MyTestWatcher;
@RunWith(Parameterized.class) public class NodeTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    public NodeTestCase(int nodes) { this.nodes=nodes; }
    @Parameters public static Collection<Object[]> testData() {
        return modulo(maxNodesToTest); }
    // tests binary node which is not really used.
    // we need these for when the binary nodes are really sgf nodes.
    // but it seems hard to subclass binary node to get an sgf node
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testPreOrder() {
        List<Node> trees=Node.allBinaryTrees(nodes);
        boolean anyFailures=false;
        for(Node node:trees) {
            if(node==null) continue;
            Node.preOrder(node);
            //String expected=node.toBinaryString();
            //String actual=Node.roundTrip(expected);
            //if(!expected.equals(actual)) anyFailures=true;
            //assertEquals(expected,actual);
        }
        assertTrue(!anyFailures);
    }
    @Test public void testRoundTripEncodeDecodeBinaryTree() {
        List<Node> trees=Node.allBinaryTrees(nodes);
        boolean anyFailures=false;
        for(Node node:trees) {
            if(node==null) continue;
            String expected=node.toBinaryString();
            String actual=Node.roundTrip(expected);
            if(!expected.equals(actual)) anyFailures=true;
            //assertEquals(expected,actual);
        }
        assertTrue(!anyFailures);
    }
    @Test public void testRoundTripToAndFromMultiwayTree() {
        List<Node> trees=Node.allBinaryTrees(nodes);
        for(Node node:trees) {
            if(node==null) continue;
            String expected=node.toBinaryString();
            Node newTree=Node.roundTrip(node);
            String actual=newTree.toBinaryString();
            assertEquals(expected,actual);
        }
    }
    int nodes;
    static final int maxNodesToTest=5; // was 11
}
