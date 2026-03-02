package model;
import org.junit.Rule;
import utilities.MyTestWatcher;
import static org.junit.Assert.*;
import java.util.List;
import org.junit.Test;
import equipment.*;
import equipment.Board.Shape;
import equipment.Board.Topology;
import io.Logging;
import model.Model.*;
import static model.Move2.*;
public class MoveTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    @Test public void testThatWeCanMakeAMoveOnANewModel() {
        Model model=new Model();
        MoveResult ok=model.move(Stone.black,new Point());
        assertEquals(MoveResult.legal,ok);
    }
    @Test public void testThatWeCanMakeABlackPassMoveOnANewModel() {
        Model model=new Model();
        Move2 passMove=Move2.blackPass;
        MoveResult ok=model.move(passMove);
        assertEquals(MoveResult.legal,ok);
        // how to test for sgf and gtp
    }
    @Test public void testThatWeCanMakeABlackResignMoveOnANewModel() {
        Model model=new Model();
        Move2 resignMove=Move2.blackResign;
        MoveResult ok=model.move(resignMove);
        assertEquals(MoveResult.legal,ok);
        // how to test for sgf and gtp
    }
    @Test public void testThatWeCanMakeAWhiteMoveOnANewModel() { // ???
        Model model=new Model();
        MoveResult ok=model.move(Stone.white,new Point());
        model.role=Role.anything;
        assertEquals(MoveResult.legal,ok);
    }
    // these need to be tested with gtp also.
    @Test public void testThatWeCanNotMakeAWhiteMoveOnANewModel() { // ???
        Model model=new Model();
        model.role=Role.playBlack;
        MoveResult ok=model.move(Stone.white,new Point());
        assertNotEquals(MoveResult.legal,ok);
    }
    @Test public void testMovesToCurrentState0() throws Exception {
        Model expected=new Model();
        expected.ensureBoard();
        List<Move2> moves=stripNullMove(expected.movesToCurrentState());
        Logging.mainLogger.info("");
        Logging.mainLogger.info(String.valueOf(moves));
        Model actual=new Model(); // maybe problems if custom width and depth?
        actual.ensureBoard();
        actual.makeMoves(moves);
        Logging.mainLogger.info(String.valueOf(expected.board()));
        Logging.mainLogger.info(String.valueOf(actual.board()));
        assertTrue(expected.board().isEqual(actual.board()));
        List<Move2> actualMoves=stripNullMove(actual.movesToCurrentState());
        assertEquals(moves,actualMoves);
    }
    @Test public void testMovesToCurrentState1() throws Exception {
        Model expected=new Model();
        expected.move(Stone.black,new Point());
        List<Move2> moves=expected.movesToCurrentState();
        Model actual=new Model(); // maybe problems if custom width and depth?
        actual.makeMoves(moves);
        assertTrue(expected.board().isEqual(actual.board()));
        List<Move2> actualMoves=actual.movesToCurrentState();
        assertEquals(moves,actualMoves);
    }
    @Test public void testFromGTPWithNullOrEmptyString() {
        Move2 expected=Move2.nullMove;
        // maybe use for resign??
        // sgf pass is [] or [tt]
        Move2 actual=fromGTP(Stone.black,null,0,0);
        assertEquals(expected,actual);
        actual=fromGTP(Stone.black,"",0,0);
        assertEquals(expected,actual);
    }
    @Test public void testResign() throws Exception {
        // meed more testing for edge cases ;ile thjis
        // also maybe for pass.
        // at least 2 problems
        // the one below with resign
        // and ???
        // maybe i can use last gtp
        Model model=new Model();
        Move2 move=Move2.blackResign;
        toGTPCoordinates(move,0,0);// returns a string?
        model.move(move);
        Logging.mainLogger.info("resign "+move);
        Move2 lastMove=model.lastMove2();
        String lastMoveGtp=model.lastMoveGTP();
        Logging.mainLogger.info(lastMove+" "+lastMoveGtp);
    }
    @Test public void testSelfCaptureDoesNotLeakToLaterMoves() {
        Model model=new Model();
        ModelTreeOps.setRoot(model,3,3,Topology.normal,Shape.normal);
        model.sgfMakeMove(Stone.black,new Point(0,1));
        model.sgfMakeMove(Stone.black,new Point(1,0));
        model.sgfMakeMove(Stone.black,new Point(2,1));
        model.sgfMakeMove(Stone.black,new Point(1,2));
        model.sgfMakeMove(Stone.white,new Point(1,1)); // self-capture at center
        assertEquals(Stone.vacant,model.board().at(1,1)); // make sure captured stone is removed.
        assertEquals(0,model.prisoners(Stone.black));
        assertEquals(1,model.prisoners(Stone.white));
        int whitePrisonersAfterSelfCapture=model.prisoners(Stone.white);
        model.sgfMakeMove(Stone.black,new Point(0,0)); // normal move, no self-capture
        assertEquals(whitePrisonersAfterSelfCapture,model.prisoners(Stone.white));
        model.sgfMakeMove(Stone.white,new Point(1,1)); // self-capture at center
        assertEquals(Stone.vacant,model.board().at(1,1));
        assertEquals(2,model.prisoners(Stone.white));
    }
    @Test public void testRepeatedSelfCaptureIsLegalInMovePath() {
        Model model=new Model();
        model.setRole(Role.anything);
        ModelTreeOps.setRoot(model,3,3,Topology.normal,Shape.normal);
        assertEquals(MoveResult.legal,model.move(Stone.black,new Point(0,1)));
        assertEquals(MoveResult.legal,model.move(Stone.black,new Point(1,0)));
        assertEquals(MoveResult.legal,model.move(Stone.black,new Point(2,1)));
        assertEquals(MoveResult.legal,model.move(Stone.black,new Point(1,2)));
        assertEquals(MoveResult.legal,model.move(Stone.white,new Point(1,1)));
        assertEquals(Stone.vacant,model.board().at(1,1));
        assertEquals(1,model.prisoners(Stone.white));
        assertEquals(MoveResult.legal,model.move(Stone.black,new Point(0,0)));
        assertEquals(MoveResult.legal,model.move(Stone.white,new Point(1,1)));
        assertEquals(Stone.vacant,model.board().at(1,1));
        assertEquals(2,model.prisoners(Stone.white));
    }
    @Test public void testRepeatedTwoStoneSelfCaptureIsLegalInMovePath() {
        Model model=new Model();
        model.setRole(Role.anything);
        ModelTreeOps.setRoot(model,4,4,Topology.normal,Shape.normal);
        assertEquals(MoveResult.legal,model.move(Stone.black,new Point(0,1)));
        assertEquals(MoveResult.legal,model.move(Stone.black,new Point(2,1)));
        assertEquals(MoveResult.legal,model.move(Stone.black,new Point(1,0)));
        assertEquals(MoveResult.legal,model.move(Stone.black,new Point(0,2)));
        assertEquals(MoveResult.legal,model.move(Stone.black,new Point(2,2)));
        assertEquals(MoveResult.legal,model.move(Stone.black,new Point(1,3)));
        assertEquals(MoveResult.legal,model.move(Stone.white,new Point(1,1)));
        assertEquals(MoveResult.legal,model.move(Stone.white,new Point(1,2)));
        assertEquals(Stone.vacant,model.board().at(1,1));
        assertEquals(Stone.vacant,model.board().at(1,2));
        assertEquals(2,model.prisoners(Stone.white));
        assertEquals(MoveResult.legal,model.move(Stone.black,new Point(3,3))); // filler so next setup is not duplicate-hash.
        assertEquals(MoveResult.legal,model.move(Stone.white,new Point(1,1)));
        assertEquals(MoveResult.legal,model.move(Stone.white,new Point(1,2)));
        assertEquals(Stone.vacant,model.board().at(1,1));
        assertEquals(Stone.vacant,model.board().at(1,2));
        assertEquals(4,model.prisoners(Stone.white));
    }
    private List<Move2> stripNullMove(List<Move2> moves) {
        if(moves!=null&&moves.size()>0&&moves.get(0).equals(Move2.nullMove)) {
            // remove these tests when the dust settles.
            Logging.mainLogger.severe("removing null move from move list!");
            moves.remove(moves.get(0));
        }
        return moves;
    }
}

