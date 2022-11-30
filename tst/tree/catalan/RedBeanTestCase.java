package tree.catalan;
import static org.junit.Assert.assertTrue;
import static tree.catalan.G2.Node.*;
import static tree.catalan.RedBean.*;
import static tree.catalan.RedBean.MNode2.print;
import org.junit.*;
import tree.catalan.G2.Node;
import tree.catalan.RedBean.MNode2;
public class RedBeanTestCase {
    @BeforeClass public static void setUpBeforeClass() throws Exception {}
    @Test public void testMWayToBinary() {
        print(mRoot,"",true);
        System.out.println(mRoot.children);
        G2.print("",bRoot);
        MNode2.processed.clear();
        Node<Character> binary=MNode2.oldFrom(mRoot);
        G2.print("",binary);
        System.out.println(deepEquals(bRoot,binary));
        System.out.println(structureDeepEquals(bRoot,binary));
        assertTrue(structureDeepEquals(bRoot,binary));
        assertTrue(deepEquals(bRoot,binary));
    }
    @Test public void testBinaryToMWay() {
        Node.processed.clear();
        MNode2<Character> mway=from(bRoot);
    }
    Node<Character> bRoot=sample();
    MNode2<Character> mRoot=sample2();
}
