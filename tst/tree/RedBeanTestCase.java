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
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class) public class RedBeanTestCase {
    @Parameters(name="{0}") public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
                {"mway to binary",Direction.MWAY_TO_BINARY},
                {"binary to mway",Direction.BINARY_TO_MWAY},
        });
    }
    public RedBeanTestCase(String name,Direction direction) {
        this.direction=direction;
    }
    @Test public void testConversion() {
        switch(direction) {
            case MWAY_TO_BINARY:
                print(mRoot,"",true);
                Logging.mainLogger.info(String.valueOf(mRoot.children));
                G2.print(bRoot,"");
                MNode.clearProcessed();
                Node<Character> binary=from(mRoot);
                G2.print(binary,"");
                assertTreesEqual("binary",bRoot,binary);
                break;
            case BINARY_TO_MWAY:
                Node.clearProcessed();
                MNode<Character> mway=from(bRoot);
                Logging.mainLogger.info("mway from binary.");
                MNode<Character> r=mway.children.get(0);
                print(r,"",true);
                Logging.mainLogger.info("mway expected");
                print(mRoot,"",true);
                assertTreesEqual("mway",mRoot,r);
                break;
            default:
                throw new AssertionError("Unhandled direction: "+direction);
        }
    }
    private void assertTreesEqual(String label,Node<Character> expected,Node<Character> actual) {
        assertTrue(structureDeepEquals(expected,actual));
        assertTrue(deepEquals(expected,actual));
        Logging.mainLogger.info(label+" expected: "+G2.pPrint(expected));
        Logging.mainLogger.info(label+" actual: "+G2.pPrint(actual));
    }
    private void assertTreesEqual(String label,MNode<Character> expected,MNode<Character> actual) {
        assertTrue(structureDeepEquals(expected,actual));
        assertTrue(deepEquals(expected,actual));
        Logging.mainLogger.info(label+" expected:");
        print(expected,"",true);
        Logging.mainLogger.info(label+" actual:");
        print(actual,"",true);
    }
    private final Direction direction;
    private enum Direction {
        MWAY_TO_BINARY,
        BINARY_TO_MWAY
    }
    final Node<Character> bRoot=binary();
    final MNode<Character> mRoot=mway();
}
