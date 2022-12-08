package tree.catalan;
import static org.junit.Assert.assertTrue;
import static tree.catalan.RedBean.*;
import static tree.catalan.RedBean.MNode2.*;
import org.junit.*;
import tree.catalan.G2.Node;
import tree.catalan.RedBean.MNode2;
public class RedBeanTestCase {
    @BeforeClass public static void setUpBeforeClass() throws Exception {}
    @Test public void testMWayToBinary() {
        print(mRoot,"",true);
        System.out.println(mRoot.children);
        G2.print(bRoot,"");
        MNode2.processed.clear();
        Node<Character> binary=MNode2.from(mRoot);
        G2.print(binary,"");
        //System.out.println(structureDeepEquals(bRoot,binary));
        //System.out.println(deepEquals(bRoot,binary));
        assertTrue(Node.structureDeepEquals(bRoot,binary));
        assertTrue(Node.deepEquals(bRoot,binary));
    }
    @Test public void testBinaryToMWay() {
        Node.processed.clear();
        MNode2<Character> mway=Node.from(bRoot);
        System.out.println("mway from binary.");
        MNode2<Character> r=mway.children.get(0);
        print(r,"",true);
        System.out.println(structureDeepEquals(mRoot,r));
        System.out.println(deepEquals(mRoot,r));
        System.out.println("mway expected");
        print(mRoot,"",true);
        assertTrue(structureDeepEquals(mRoot,r));
        assertTrue(deepEquals(mRoot,r));

    }
    final Node<Character> bRoot=binary();
    final MNode2<Character> mRoot=mway();
}
