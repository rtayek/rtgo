package tree.catalan;
import static org.junit.Assert.assertTrue;
import static tree.catalan.G2.Node.from;
import static tree.catalan.G2.Node.structureDeepEquals;
import static tree.catalan.RedBean.*;
import static tree.catalan.RedBean.MNode2.deepEquals;
import static tree.catalan.RedBean.MNode2.print;
import static tree.catalan.RedBean.MNode2.structureDeepEquals;
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
        Node<Character> binary=MNode2.oldFrom(mRoot);
        G2.print(binary,"");
        //System.out.println(structureDeepEquals(bRoot,binary));
        //System.out.println(deepEquals(bRoot,binary));
        assertTrue(structureDeepEquals(bRoot,binary));
        //assertTrue(deepEquals(bRoot,binary));
    }
    @Test public void testBinaryToMWay() {
        G2.print(bRoot,"");
        print(mRoot,"",true);
        System.out.println(mRoot.children);
        Node.processed.clear();
        MNode2<Character> mway=from(bRoot,null);
        print(mway,"",true);
        System.out.println(structureDeepEquals(mRoot,mway));
        System.out.println(deepEquals(mRoot,mway));
        assertTrue(structureDeepEquals(mRoot,mway));
        assertTrue(deepEquals(mRoot,mway));
        
    }
    Node<Character> bRoot=binary();
    MNode2<Character> mRoot=mway();
}
