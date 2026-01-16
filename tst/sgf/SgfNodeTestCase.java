package sgf;
import io.Logging;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.Test;
import equipment.*;
public class SgfNodeTestCase extends AbstractWatchedTestCase {
    @Test public void testSgfCoordinates() {
        SgfNode expected=SgfTestSupport.nodeWithProperty(P.B,"AB"); // what is AB?
        String string=expected.sgfProperties.get(0).list().get(0);
        Point point=Coordinates.fromSgfCoordinates(string,Board.standard);
        String string2=Coordinates.toSgfCoordinates(point,Board.standard);
        SgfNode actual=SgfTestSupport.nodeWithProperty(P.B,string2);
        assertEquals(expected,actual);
    }
    @Test public void testHasAMoveFlag() {
        assertMoveFlags(P.B,true,true,true);
    }
    @Test public void testHasAMoveTypeFlag() {
        assertMoveFlags(P.BM,true,false,false);
    }
    @Test public void testBothFlagsFalse() {
        assertMoveFlagsOnRoot(P.AB,false,false);
    }
    @Test public void testConstructor() { SgfNode sgfNode=new SgfNode(); sgfNode.sgfProperties=new ArrayList<>(); }
    @Test public void testRT() {
        MNode mRoot=new MNode(null);
        try {
            mRoot.sgfProperties().add(SgfTestSupport.property(P.RT,"Tgo root"));
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        SgfNode sgfNode=mRoot.toBinaryTree();
        Logging.mainLogger.info(String.valueOf(sgfNode));
    }
    private void assertMoveFlags(P id,boolean expectedMoveType,boolean expectedMove,boolean logChildren) {
        SgfNode node=SgfTestSupport.nodeWithProperty(id,"inside the brackets");
        assertEquals(expectedMoveType,node.hasAMoveType);
        assertEquals(expectedMove,node.hasAMove);
        node.setFlags();
        node.checkFlags();
        MNode mNode=MNode.toGeneralTree(node);
        for(MNode child:mNode.children()) {
            if(logChildren) Logging.mainLogger.info(String.valueOf(child));
            child.setFlags();
            child.checkFlags();
            assertEquals(expectedMoveType,child.hasAMoveType());
            assertEquals(expectedMove,child.hasAMove());
        }
    }
    private void assertMoveFlagsOnRoot(P id,boolean expectedMoveType,boolean expectedMove) {
        SgfNode node=SgfTestSupport.nodeWithProperty(id,"inside the brackets");
        assertEquals(expectedMoveType,node.hasAMoveType);
        assertEquals(expectedMove,node.hasAMove);
        MNode mNode=MNode.toGeneralTree(node);
        assertEquals(expectedMoveType,mNode.hasAMoveType());
        assertEquals(expectedMove,mNode.hasAMove());
    }
}
