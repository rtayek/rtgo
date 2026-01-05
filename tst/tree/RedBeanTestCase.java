package tree;
import io.Logging;
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
        Logging.mainLogger.info(String.valueOf(mRoot.children));
        G2.print(bRoot,"");
        MNode.processed.clear();
        Node<Character> binary=from(mRoot);
        G2.print(binary,"");
        //Logging.mainLogger.info(structureDeepEquals(bRoot,binary));
        //Logging.mainLogger.info(deepEquals(bRoot,binary));
        assertTrue(structureDeepEquals(bRoot,binary));
        assertTrue(deepEquals(bRoot,binary));
        Logging.mainLogger.info(String.valueOf(G2.pPrint(bRoot)));
        Logging.mainLogger.info(String.valueOf(G2.pPrint(binary)));
    }
    @Test public void testBinaryToMWay() {
        Node.processed.clear();
        MNode<Character> mway=from(bRoot);
        Logging.mainLogger.info("mway from binary.");
        MNode<Character> r=mway.children.get(0);
        print(r,"",true);
        Logging.mainLogger.info(String.valueOf(structureDeepEquals(mRoot,r)));
        Logging.mainLogger.info(String.valueOf(deepEquals(mRoot,r)));
        Logging.mainLogger.info("mway expected");
        print(mRoot,"",true);
        assertTrue(structureDeepEquals(mRoot,r));
        assertTrue(deepEquals(mRoot,r));
    }
    final Node<Character> bRoot=binary();
    final MNode<Character> mRoot=mway();
}
