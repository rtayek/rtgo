package controller;
//maybe shove sgf text with a new gtp command?
//maybe: restore <sgf text>?
// shove a tree of games to remote model.
import static org.junit.Assert.*;
import static sgf.Parser.getSgfData;
import java.io.StringReader;
import java.util.List;
import org.junit.*;
import equipment.*;
import game.Game;
import model.*;
import utilities.MyTestWatcher;
public class GTPShoveTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    // how to shove all variations?
    // maybe just invent loadSGF command and send it!
    @Before public void setUp() throws Exception {
        // parameterize this and use parser map?
        expected.setRoot(5,5);
        Board board=Board.factory.create(5);
        expected.setBoard(board);
        int n=expected.movesToGenerate();
        Model.generateAndMakeMoves(expected,n);
        Move pass=expected.passMove();
        expected.move(pass);
        pass=expected.passMove();
        expected.move(pass);
    }
    @After public void tearDown() throws Exception {}
    @Test public void testInitialBoardInNewModel() {
        Model model=new Model();
        assertNotNull(model.board()); // was returning null
    }
        
    @Test public void testPushGTPMovesToCurrentStateDirectOneAtATime() throws Exception {
        Model actual=MoveHelper.pushGTPMovesToCurrentStateDirect(expected,true);
        System.out.println(expected);
        System.out.println(actual);
        assertTrue(actual.board().isEqual(expected.board()));
    }
    @Test public void testPushGTPMovesToCurrentStateDirectList() throws Exception {
        Model actual=MoveHelper.pushGTPMovesToCurrentStateDirect(expected,false);
        assertTrue(actual.board().isEqual(expected.board()));
    }
    @Test public void testPushGTPMovesToEachStateDirectBottomUp() throws Exception {
        expected.bottom();
        while(Navigate.up.do_(expected)) {
            Model actual=MoveHelper.pushGTPMovesToCurrentStateDirect(expected,false);
            assertTrue(actual.board().isEqual(expected.board()));
        }
    }
    @Test public void testPushGTPMovesToEachStateDirectTopDown() throws Exception {
        expected.top();
        while(Navigate.down.do_(expected)) {
            Model actual=MoveHelper.pushGTPMovesToCurrentStateDirect(expected,false);
            assertTrue(actual.board().isEqual(expected.board()));
        }
    }
    @Test public void testRestoreAndShoveMainLineDirect() throws Exception {
        String sgf=getSgfData("simpleWithVariations");
        Model original=new Model();
        original.restore(new StringReader(sgf));
        original.bottom();
        Model model=MoveHelper.pushGTPMovesToCurrentStateDirect(original,false);
        assertTrue(model.board().isEqual(original.board()));
    }
    @Test public void testMainlineMovesToCurrentStateDirect() throws Exception {
        expected.up();
        expected.up();
        List<String> gtpMoves=expected.gtpMovesToCurrentState();
        Model actual=new Model("actual");
        //actual.ensureBoard();
        if(expected.board()!=null) { // normally no access to both of these at the same time
            actual.setRoot(expected.board().width(),expected.board().depth());
            Board board=Board.factory.create(expected.board().width(),expected.board().depth());
            actual.setBoard(board);

            // probably need to set other stuff like shape etc.
        } 
        Response[] responses=GTPBackEnd.runCommands(gtpMoves,actual,justRun);
        for(Response response:responses) assertTrue(response.isOk());
        System.out.println(expected);
        System.out.println(actual);
        assertTrue(expected.board().isEqual(actual.board()));
        List<Move2> moves=expected.movesToCurrentState(); //current line
        List<Move2> actualMoves=actual.movesToCurrentState();
        assertEquals(moves,actualMoves);
    }
    @Test public void testPushOneGTPMoveDirect() throws Exception {
        // do to top and down instead of new model?
        Model model=new Model();
        model.setRoot(5,5);
        model.move(Stone.black,new Point());
        Model actual=MoveHelper.pushGTPMovesToCurrentStateDirect(model,false);
        assertTrue(actual.board().isEqual(model.board()));
    }
    @Test public void testPushOneGTPMoveBoth() throws Exception {
        // do to top and down instead of new model?
        Model model=new Model("model");
        // maybe give backend it's own name?
        model.setRoot(5,5);
        model.move(Stone.black,new Point());
        Model actual=MoveHelper.pushGTPMovesToCurrentStateBoth(model,true);
        assertTrue(actual.board().isEqual(model.board()));
    }
    @Test public void testPushGTPMovesBoth() throws Exception {
        Model actual=MoveHelper.pushGTPMovesToCurrentStateBoth(expected,true);
        assertTrue(actual.board().isEqual(expected.board()));
    }
    Model expected=new Model("model");
    GameFixture game;
    boolean justRun=true;
}
