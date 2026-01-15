package sgf;
import io.Logging;
import static org.junit.Assert.*;
import java.io.File;
import java.util.*;
import org.junit.*;
import equipment.*;
import equipment.Board.Topology;
import io.IOs;
import model.Model;
import model.Model.*;
import static model.Model.*;
import model.ModelIo;
import model.Move2;
import model.Move2.MoveType;
public class OldModelTestCase extends AbstractWatchedTestCase {
    // random seems to work!
    @Test public void testResignOutOfOrder() {
        model.move(new Move2(MoveType.move,Stone.black,new Point()));
        model.move(Move2.blackPass);
    }
    @Test public void testHash() {
        model.move(new Move2(MoveType.move,Stone.black,new Point()));
        model.move(Move2.whitePass);
        model.move(Move2.blackPass);
        // test for collision problem here
        // how?
    }
    @Test public void testgenerateAndMakeMoveTurn() {
        Stone who=model.turn();
        String move=model.generateAndMakeMove();
        if(move!=null) assertEquals(who.otherColor(),model.turn());
        else assertEquals(who,model.turn());
    }
    @Test public void testgenerateAndMakeMovesPreserveTurn() {
        assertGenerateAndMakeMovesPreserveTurn(null,1);
        assertGenerateAndMakeMovesPreserveTurn(null,2);
        assertGenerateAndMakeMovesPreserveTurn(5,3);
    }
    @Test public void testgenerateMoves() {
        model.setRoot(5,5);
        int n=model.movesToGenerate();
        Model.generateAndMakeMoves(model,n);
        //List<Move> expectedMoves=Model.movesToCurrentState(model);
        assertEquals(n,model.moves());
    }
    @Test public void testgenerateMovesTime() {
        for(int i=5;i<=19;i++) {
            model.setRoot(i,i);
            int n=model.movesToGenerate();
            Model.generateAndMakeMoves(model,n);
            List<Move2> expectedMoves=model.movesToCurrentState();
            Model actual=newModelWithRoot(i,true);
            assertMovesRoundTrip(model,actual,expectedMoves,true);
        }
    }
    @Test public void testGenerateSomeSillyMoves() { Model.generateSillyMoves(model,2); }
    @Test public void testGenerateAndReplayAllSillyMoves() {
        int n=model.board().width()*model.board().depth();
        Model.generateSillyMoves(model,n-1);
        List<Move2> expectedMoves=model.movesToCurrentState();
        Logging.mainLogger.info(String.valueOf(expectedMoves));
        Model actual=new Model();
        assertMovesRoundTrip(model,actual,expectedMoves,false);
    }
    @Test public void testGenerateSillyMovesTime() {
        for(int i=5;i<=19;i++) {
            if(i>5) break; // debuging
            model.setRoot(i,i);
            int n=model.movesToGenerate();
            Logging.mainLogger.info("start");
            Model.generateSillyMoves(model,n);
            List<Move2> expectedMoves=model.movesToCurrentState();
            Logging.mainLogger.info(String.valueOf(expectedMoves));
            Model actual=newModelWithRoot(i,false);
            assertMovesRoundTrip(model,actual,expectedMoves,false);
        }
    }
    @Test public void testReplayMoves() {
        model.setRoot(19,19);
        int n=model.movesToGenerate();
        Model.generateAndMakeMoves(model,n);
        List<Move2> expectedMoves=model.movesToCurrentState();
        model.ensureBoard();
        Model actual=newModelWithRoot(19,false);
        assertMovesRoundTrip(model,actual,expectedMoves,false);
    }
    @Test public void testRandomMove() throws Exception {
        Model model=newModelWithRoot(5,false);
        int n=model.movesToGenerate();
        for(int i=0;i<n;++i) {
            Point point=model.generateRandomMove();
            model.move(i%2==0?Stone.black:Stone.white,point);
            // how to test?
        }
    }
    @Test public void testReplayRandomMoves() throws Exception {
        Model model=newModelWithRoot(5,true);
        int n=model.movesToGenerate();
        Model.generateRandomMoves(model,n);
        List<Move2> moves=model.movesToCurrentState();
        Model actual=newModelWithRoot(5,true);
        actual.makeMoves(moves);
        model.board().isEqual(actual.board());
    }
    @Test public void testReplayRandomMovesList() throws Exception {
        Model model=newModelWithRoot(5,true);
        int n=model.movesToGenerate();
        List<Point> points=Model.generateRandomMovesInList(model,n);
        Model actual=newModelWithRoot(5,false);
        for(Point point:points) model.move(new Move2(MoveType.move,model.turn(),point));
        model.board().isEqual(actual.board());
    }
    @Test public void testPushAndPop() {
        model.setBoard(Board.factory.create(9));
        int expected=model.board().id();
        model.push();
        // model.state.node=model.state.node.children.get(0);
        assertNotEquals("we got a new board",expected,model.board().id());
        model.pop();
        int actual=model.board().id();
        assertEquals(expected,actual);
    }
    @Test public void testListMoves() {
        Model model=new Model();
        // 19x19?
        List<Move2> moves=model.movesToCurrentState();
        assertTrue(moves==null||moves.size()==0||moves.size()==1&&moves.get(0).equals(Move2.nullMove));
        model.setRoot(5,5);
        moves=model.movesToCurrentState();
        assertTrue(moves==null||moves.size()==0||moves.size()==1&&moves.get(0).equals(Move2.nullMove));
        model.move(model.turn(),new Point());
        // confused. problem when first node or top is wierd?
        moves=model.movesToCurrentState();
        assertTrue(moves.size()==1);
    }
    @Test public void testListMoves2() {
        model.setRoot(5,5);
        model.move(Stone.black,new Point());
        model.move(Stone.white,new Point(1,1));
        model.move(Stone.black,new Point(2,2));
        List<Move2> moves=model.movesToCurrentState();
        List<Move2> expected=Arrays.asList(new Move2(MoveType.move,Stone.black,new Point(0,0)),
                new Move2(MoveType.move,Stone.white,new Point(1,1)),
                new Move2(MoveType.move,Stone.black,new Point(2,2))); //"[move (0,0), move (1,1), move (2,2)]";
        assertEquals(expected,moves);
    }
    @Test public void testCopyConstructorWithEmpty() {
        // this is failing.
        // ex: (;FF[4]GM[1]AP[RTGO]C[root]SZ[19])
        // ac: (;RT[Tgo root];FF[4]GM[1]AP[RTGO]C[root]SZ[19])
        // similar to variations below.
        assertCopyConstructorRoundTrip(Parser.empty,false);
    }
    @Test public void testCopyConstructorWithVariationOfAVariation() {
        // this is failing.
        // actual has 2 copyies of ;RT[Tgo root] in the first node?
        assertCopyConstructorRoundTrip(Parser.variationOfAVariation,true);
    }
    @Test public void testSquares() {
        for(int width=3;width<=Model.LargestBoardSize;width++) for(int depth=3;depth<=Model.LargestBoardSize;depth++) {
            Board board=Board.factory.create(width,depth,Topology.normal);
            List<Point> points=Board.squares(1,width,depth);
            for(Point point:points) assertTrue(board.isOnBoard(point));
        }
    }
    @Test public void testHole() {
        for(int n=0;n<=Board.standard/2;++n) {
            List<Point> points=Board.holeInCenter(n,Board.standard,Board.standard);
            assertEquals((2*n+1)*(2*n+1),points.size());
            // maybe only works for odd sizes?
        }
    }
    @Test public void testSave() {
        File file=new File("tmp/saved.sgf");
        if(file.exists()) file.delete();
        boolean ok=model.save(IOs.toWriter(file));
        assertTrue(ok);
        assertTrue(file.exists());
    }
    @Test public void testIsLegalMove() {
        model.setRoot();
        Move2 move=new Move2(MoveType.move,model.turn(),new Point());
        MoveResult actual=model.isLegalMove(move);
        assertEquals(MoveResult.legal,actual);
    }
    @Test public void testRole() {
        for(Model.Role role:Model.Role.values()) {
            model.setRole(role);
            assertEquals(role,model.role());
        }
    }
    @Test public void testWithSgfFile() {
        // hangs when ignore is remove from test go to node!
        String key="manyFacesTwoMovesAtA1AndR16";
        String sgfString=SgfTestSupport.loadExpectedSgf(key);
        MNode root=SgfTestIo.quietLoadMNode(sgfString);
        model.setRoot(root);
        if(model.board()!=null) {
            int expected=model.board().id();
            model.push();
            model.do_(model.currentNode().children().get(0));
            assertNotEquals(expected,model.board().id());
            model.pop(); 
            // fails because do2 does not do a push  
            // first child is a setup node
            // "(;GM[1]FF[4]VW[]AP[Many Faces of Go:12.022]SZ[19]HA[0]ST[0]PB[ray]PW[ray]DT[2015-03-31]KM[7.5]RU[Chinese]BR[2 Dan]WR[2 Dan];B[as]BL[50]WL[60];W[qd]BL[1800]WL[1727])";
            int actual=model.board().id();
            // what is this actually testing?
            assertEquals("we get the same board id.",expected,actual);
        } else Logging.mainLogger.info(key+" board is null.");
    }
    private Model newModelWithRoot(int size,boolean ensureBoard) {
        Model model=new Model();
        model.setRoot(size,size);
        if(ensureBoard) model.ensureBoard();
        return model;
    }
    private void assertGenerateAndMakeMovesPreserveTurn(Integer size,int moves) {
        if(size!=null) model.setRoot(size,size);
        Stone who=model.turn();
        Model.generateAndMakeMoves(model,moves);
        Stone expected=moves%2==0?who:who.otherColor();
        assertEquals(expected,model.turn());
    }
    private void assertMovesRoundTrip(Model source,Model actual,List<Move2> expectedMoves,boolean ensureBoards) {
        if(ensureBoards) actual.ensureBoard();
        actual.makeMoves(expectedMoves);
        if(ensureBoards) source.ensureBoard();
        source.board().isEqual(actual.board());
        List<Move2> actualMoves=actual.movesToCurrentState();
        assertEquals(expectedMoves,actualMoves); // round trip
    }
    private void assertCopyConstructorRoundTrip(String sgf,boolean log) {
        Model model=new Model();
        String expected=ModelTestIo.restoreAndSave(model,sgf,"first save fails");
        Model copy=new Model(model,model.name);
        String actual=ModelTestIo.save(copy,"second save fails");
        if(log) {
            Logging.mainLogger.info("ex: "+expected);
            Logging.mainLogger.info("ac: "+actual);
        }
        assertEquals(expected,actual);
    }
    private void assertRoundTripHasRt(Model model,boolean expectedBefore,boolean expectedAfter) {
        boolean hasRT=hasRT(model.root());
        assertEquals("unexpected RT before round trip",expectedBefore,hasRT);
        String sgfString=ModelTestIo.save(model);
        model=new Model();
        ModelTestIo.restore(model,sgfString);
        hasRT=hasRT(model.root());
        assertEquals("unexpected RT after round trip",expectedAfter,hasRT);
    }
    @Test public void testRTWithNoMoves() {
        Model model=new Model();
        assertRoundTripHasRt(model,false,true);
    }
    @Test public void testRT() {
        Model model=new Model();
        model.move(Stone.black,new Point());
        assertRoundTripHasRt(model,false,true);
    }
    /*@Test*/ public void testSgfFileFromLittleGolem() {
        // Node root=quietLoad(new File("url.sgf"));
        Model model=new Model();
        ModelIo.restore(model,new File("url.sgf"));
    }
    Model model=new Model();
    {
        model.ensureBoard();
    }
}
