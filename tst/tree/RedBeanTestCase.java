package tree;
import static org.junit.Assert.assertTrue;
import static tree.MNode.deepEquals;
import static tree.MNode.from;
import static tree.MNode.print;
import static tree.MNode.structureDeepEquals;
import static tree.Node.deepEquals;
import static tree.Node.from;
import static tree.Node.structureDeepEquals;
import static tree.RedBean.*;
import org.junit.*;
public class RedBeanTestCase {
    @BeforeClass public static void setUpBeforeClass() throws Exception {}
    @Test public void testMWayToBinary() {
        print(mRoot,"",true);
        System.out.println(mRoot.children);
        G2.print(bRoot,"");
        MNode.processed.clear();
        Node<Character> binary=from(mRoot);
        G2.print(binary,"");
        //System.out.println(structureDeepEquals(bRoot,binary));
        //System.out.println(deepEquals(bRoot,binary));
        assertTrue(structureDeepEquals(bRoot,binary));
        assertTrue(deepEquals(bRoot,binary));
    }
    @Test public void testBinaryToMWay() {
        Node.processed.clear();
        MNode<Character> mway=from(bRoot);
        System.out.println("mway from binary.");
        MNode<Character> r=mway.children.get(0);
        print(r,"",true);
        System.out.println(structureDeepEquals(mRoot,r));
        System.out.println(deepEquals(mRoot,r));
        System.out.println("mway expected");
        print(mRoot,"",true);
        assertTrue(structureDeepEquals(mRoot,r));
        assertTrue(deepEquals(mRoot,r));
    }
    final Node<Character> bRoot=binary();
    final MNode<Character> mRoot=mway();
}
