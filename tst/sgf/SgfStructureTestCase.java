package sgf;

import io.Logging;
import static model.MNodeAcceptor.MNodeFinder.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import equipment.Board;
import equipment.Coordinates;
import equipment.Point;
import equipment.Stone;
import model.Model;
import model.MNodeAcceptor.MakeList;
import utilities.Iterators.Longs;
import utilities.TestKeys;

public class SgfStructureTestCase extends AbstractSgfKeyedTestCase implements RedBeanKeyed {
    @Before public void setUpFixture() throws Exception {
        expectedSgf=SgfTestSupport.loadExpectedSgf(key);
        if(expectedSgf==null) { return; }
        root1=restoreExpectedMNode();
        root2=restoreExpectedMNode();
        MNode.label(root1,new Longs());
        MNode.label(root2,new Longs());
        list1=MakeList.toList(root1);
        list2=MakeList.toList(root2);
    }

    @Override protected Object defaultKey() {
        return TestKeys.manyFacesTwoMovesAtA1AndR16OnA9by9Board;
    }

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

    @Test public void testConstructor() {
        SgfNode sgfNode=new SgfNode();
        sgfNode.sgfProperties=new ArrayList<>();
    }

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

    @Test public void testNodeNotInTree() {
        MNode mNode=new MNode(null);
        assertNotFound(mNode,root2);
    }

    @Test public void testFindSelfWithEqualsPredicate() {
        assertFound(root1,root1,equalsPredicate);
    }

    @Test public void testFindSelfWithLabelPredicate() {
        assertFoundWithLabel(root1,root1);
    }

    @Ignore @Test public void testFindRootWithEqualsPredicate() {
        // should fail because we use equals which is just ==?
        MNodeFinder finder=MNodeFinder.find(root1,root2,equalsPredicate);
        assertTrue(finder.ancestors.size()>0);
        assertEquals(root1.label(),finder.found.label()); // might not always be labeled?
    }

    @Test public void testFindRootWithLabelPredicate() {
        assertFoundWithLabel(root1,root2);
    }

    @Test public void testFindFirstChildWithLabelPredicate() {
        MNode child=root1.children().iterator().next();
        assertFoundWithLabel(child,root2);
    }

    @Test public void testFindNodeWithLabelPredicate() {
        for(MNode node1:list1) {
            MNode remote=new MNode(null,node1.sgfProperties());
            remote.setLabel(node1.label());
            assertFoundWithLabel(remote,root2);
        }
    }

    @Test public void testFinderWithSimple() {
        SgfNode games=SgfTestSupport.restoreFromKey(TestKeys.simpleWithVariations);
        SgfTestSupport.assertFinderMatches(games);
    }

    @Test public void testFinderWith3Moves() {
        Model model=new Model();
        model.move(Stone.black,new Point(0,0));
        model.move(Stone.white,new Point(0,1)); // fails if black - check later
        model.move(Stone.black,new Point(0,2));
        SgfTestSupport.assertFinderMatches(model.root().toBinaryTree());
    }

    @Test public void testTwoMovesPath() {
        SgfMovesPath acceptor=new SgfMovesPath();
        restoreAndTraverse(acceptor);
        assertEquals(2,acceptor.moves.size());
    }

    @Test public void testTwoMoves() {
        SgfNode games=restoreAndTraverse(new SgfNoOpAcceptor());
        SgfNode move1=games.left;
        SgfNode move2=games.left.left;
        SgfProperty property1=move1.sgfProperties.get(0);
        String m1=property1.list().get(0);
        SgfProperty property2=move2.sgfProperties.get(0);
        String m2=property2.list().get(0);
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

    private static MNodeFinder assertFound(MNode target,MNode root,BiPredicate<MNode,MNode> predicate) {
        MNodeFinder finder=MNodeFinder.find(target,root,predicate);
        assertTrue(finder.ancestors.size()>0);
        return finder;
    }

    private static void assertFoundWithLabel(MNode target,MNode root) {
        MNodeFinder finder=assertFound(target,root,labelPredicate);
        assertEquals(target.label(),finder.found.label());
    }

    private static void assertNotFound(MNode target,MNode root) {
        MNodeFinder finder=MNodeFinder.find(target,root,equalsPredicate);
        assertTrue(finder.ancestors.size()==0);
    }

    private SgfNode restoreAndTraverse(SgfAcceptor acceptor) {
        SgfNode games=restoreExpectedSgf();
        if(games!=null) SgfTestSupport.traverse(acceptor,games);
        return games;
    }

    MNode root1;
    MNode root2;
    List<MNode> list1;
    List<MNode> list2;
}
