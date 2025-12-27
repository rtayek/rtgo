package model;
import model.MoveHelper;
import static model.MoveHelper.*;
import static org.junit.Assert.*;
import java.util.List;
import org.junit.*;
import equipment.*;
import io.Logging;
import model.Model.*;
import utilities.MyTestWatcher;
public class MoveTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testThatWeCanMakeAMoveOnANewModel() {
        Model model=new Model();
        MoveResult ok=model.move(Stone.black,new Point());
        assertEquals(MoveResult.legal,ok);
    }
    @Test public void testThatWeCanMakeABlackPassMoveOnANewModel() {
        Model model=new Model();
        Move passMove=Move.blackPass;
        MoveResult ok=model.move(passMove);
        assertEquals(MoveResult.legal,ok);
        // how to test for sgf and gtp
    }
    @Test public void testThatWeCanMakeABlackResignMoveOnANewModel() {
        Model model=new Model();
        Move resignMove=Move.blackResign;
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
        List<Move> moves=expected.movesToCurrentState();
        if(moves!=null&&moves.size()>0&&moves.get(0).equals(Move.nullMove)) {
            // remove these tests when the dust settles.
            Logging.mainLogger.severe("removing null move from move list!");
            moves.remove(moves.get(0));
        }
        System.out.println();
        System.out.println(moves);
        Model actual=new Model(); // maybe problems if custom width and depth?
        actual.ensureBoard();
        actual.makeMoves(moves);
        System.out.println(expected.board());
        System.out.println(actual.board());
        assertTrue(expected.board().isEqual(actual.board()));
        List<Move> actualMoves=actual.movesToCurrentState();
        if(actualMoves!=null&&actualMoves.size()>0&&actualMoves.get(0).equals(Move.nullMove)) {
            Logging.mainLogger.severe("removing null move from move list!");
            actualMoves.remove(actualMoves.get(0));
        }
        assertEquals(moves,actualMoves);
    }
    @Test public void testMovesToCurrentState1() throws Exception {
        Model expected=new Model();
        expected.move(Stone.black,new Point());
        List<Move> moves=expected.movesToCurrentState();
        Model actual=new Model(); // maybe problems if custom width and depth?
        actual.makeMoves(moves);
        assertTrue(expected.board().isEqual(actual.board()));
        List<Move> actualMoves=actual.movesToCurrentState();
        assertEquals(moves,actualMoves);
    }
    @Test public void testFromGTPWithNullOrEmptyString() {
        Move expected=Move.nullMove;
        // maybe use for resign??
        // sgf pass is [] or [tt]
        Move actual=toLegacyMove(fromGTP(Stone.black,null,0,0));
        assertEquals(expected,actual);
        actual=toLegacyMove(fromGTP(Stone.black,"",0,0));
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
        Move move=Move.blackResign;
        move.toGTPCoordinates(0,0); // returns a string?
        model.move(move);
        System.out.println("resign "+move);
        Move lastMove=model.lastMove();
        String lastMoveGtp=model.lastMoveGTP();
        System.out.println(lastMove+" "+lastMoveGtp);
    }
}
