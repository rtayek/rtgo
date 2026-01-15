package sgf;
import io.Logging;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import equipment.*;
public class SgfNodeTestCase extends AbstractWatchedTestCase {
    private static SgfProperty property(P id,String value) {
        return new SgfProperty(id,Arrays.asList(new String[] {value}));
    }
    private static SgfNode nodeWithProperty(P id,String value) {
        SgfNode node=new SgfNode();
        node.add(property(id,value));
        return node;
    }
    @Test public void testSgfCoordinates() {
        SgfNode expected=nodeWithProperty(P.B,"AB"); // what is AB?
        String string=expected.sgfProperties.get(0).list().get(0);
        Point point=Coordinates.fromSgfCoordinates(string,Board.standard);
        String string2=Coordinates.toSgfCoordinates(point,Board.standard);
        SgfNode actual=nodeWithProperty(P.B,string2);
        assertEquals(expected,actual);
    }
    @Test public void testHasAMoveFlag() {
        SgfNode node=nodeWithProperty(P.B,"inside the brackets");
        assertTrue(node.hasAMoveType);
        assertTrue(node.hasAMove);
        node.setFlags();
        node.checkFlags();
        MNode mNode=MNode.toGeneralTree(node);
        for(MNode child:mNode.children()) {
            Logging.mainLogger.info(String.valueOf(child));
            child.setFlags();
            child.checkFlags();
            assertTrue(child.hasAMoveType());
            assertTrue(child.hasAMove());
        }
    }
    @Test public void testHasAMoveTypeFlag() {
        SgfNode node=nodeWithProperty(P.BM,"inside the brackets");
        assertTrue(node.hasAMoveType);
        assertFalse(node.hasAMove);
        MNode mNode=MNode.toGeneralTree(node);
        for(MNode child:mNode.children()) {
            child.setFlags();
            assertTrue(child.hasAMoveType());
            assertFalse(child.hasAMove());
        }
    }
    @Test public void testBothFlagsFalse() {
        SgfNode node=nodeWithProperty(P.AB,"inside the brackets");
        assertFalse(node.hasAMoveType);
        assertFalse(node.hasAMove);
        MNode mNode=MNode.toGeneralTree(node);
        assertFalse(mNode.hasAMoveType());
        assertFalse(mNode.hasAMove());
    }
    @Test public void testConstructor() { SgfNode sgfNode=new SgfNode(); sgfNode.sgfProperties=new ArrayList<>(); }
    @Test public void testRT() {
        MNode mRoot=new MNode(null);
        try {
            mRoot.sgfProperties().add(property(P.RT,"Tgo root"));
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        SgfNode sgfNode=mRoot.toBinaryTree();
        Logging.mainLogger.info(String.valueOf(sgfNode));
    }
}
