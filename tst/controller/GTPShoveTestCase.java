package controller;
import static model.Move.getMovesAndPush;
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
        int n=expected.movesToGenerate();
        Model.generateAndMakeMoves(expected,n);
        Move pass=expected.passMove();
        expected.move(pass);
        pass=expected.passMove();
        expected.move(pass);
    }
    @After public void tearDown() throws Exception {}
    @Test public void testInitialBoardInNewModel() { Model model=new Model(); assertNotNull(model.board()); }
    @Test public void testPushGTPMovesToCurrentStateDirectOneAtATime() throws Exception {
        Model actual=Move.pushGTPMovesToCurrentStateDirect(expected,true);
        assertTrue(actual.board().isEqual(expected.board()));
    }
    @Test public void testPushGTPMovesToCurrentStateDirectList() throws Exception {
        Model actual=Move.pushGTPMovesToCurrentStateDirect(expected,false);
        assertTrue(actual.board().isEqual(expected.board()));
    }
    @Test public void testPushGTPMovesToEachStateDirectBottomUp() throws Exception {
        expected.bottom();
        while(Navigate.up.do_(expected)) {
            Model actual=Move.pushGTPMovesToCurrentStateDirect(expected,false);
            assertTrue(actual.board().isEqual(expected.board()));
        }
    }
    @Test public void testPushGTPMovesToEachStateDirectTopDown() throws Exception {
        expected.top();
        while(Navigate.down.do_(expected)) {
            Model actual=Move.pushGTPMovesToCurrentStateDirect(expected,false);
            assertTrue(actual.board().isEqual(expected.board()));
        }
    }
    @Test public void testRestoreAndShoveMainLineDirect() throws Exception {
        String sgf=getSgfData("simpleWithVariations");
        Model original=new Model();
        original.restore(new StringReader(sgf));
        original.bottom();
        Model model=Move.pushGTPMovesToCurrentStateDirect(original,false);
        assertTrue(model.board().isEqual(original.board()));
    }
    @Test public void testMainlineMovesToCurrentStateDirect() throws Exception {
        expected.up();
        expected.up();
        List<String> gtpMoves=expected.gtpMovesToCurrentState();
        Model actual=new Model("actual");
        if(expected.board()!=null) { // normally no access to both of these at the same time
            actual.setRoot(expected.board().width(),expected.board().depth());
            // probably need to set other stuff like shape etc.
        }
        Response[] responses=GTPBackEnd.runCommands(gtpMoves,actual,justRun);
        for(Response response:responses) assertTrue(response.isOk());
        assertTrue(expected.board().isEqual(actual.board()));
        List<Move> moves=expected.movesToCurrentState(); //current line
        List<Move> actualMoves=actual.movesToCurrentState();
        assertEquals(moves,actualMoves);
    }
    @Test public void testPushOneGTPMoveDirect() throws Exception {
        // do to top and down instead of new model?
        Model model=new Model();
        model.setRoot(5,5);
        model.move(Stone.black,new Point());
        Model actual=Move.pushGTPMovesToCurrentStateDirect(model,false);
        assertTrue(actual.board().isEqual(model.board()));
    }
    @Test public void testPushOneGTPMoveBoth() throws Exception {
        // do to top and down instead of new model?
        Model model=new Model("model");
        // maybe give backend it's own name?
        model.setRoot(5,5);
        model.move(Stone.black,new Point());
        Model actual=Move.pushGTPMovesToCurrentStateBoth(model,true);
        assertTrue(actual.board().isEqual(model.board()));
    }
    @Test public void testPushGTPMovesBoth() throws Exception {
        Model actual=Move.pushGTPMovesToCurrentStateBoth(expected,true);
        assertTrue(actual.board().isEqual(expected.board()));
    }
    @Test public void testPushInGameDuplex() throws Exception { //1 asffadfpadsd[qsdk,q'sdq[
        // consolidate this!
        game=Game.setupLocalGameForShove(expected);
        //
        Model black=game.blackFixture.backEnd.model;
        Model white=game.whiteFixture.backEnd.model;
        if(expected.board()!=null) { // normally no access to both of these at the same time
            black.setRoot(expected.board().width(),expected.board().depth());
            white.setRoot(expected.board().width(),expected.board().depth());
            // probably need to set other stuff like shape etc.
        }
        getMovesAndPush(game.blackFixture.frontEnd,expected,true); // maybe all at once?
        getMovesAndPush(game.whiteFixture.frontEnd,expected,true);
        // GTPBackEnd.sleep(100); ???
        //assertTrue(recorder.board().isEqual(expected.board()));
        assertTrue(black.board().isEqual(expected.board()));
        assertTrue(white.board().isEqual(expected.board()));
    }
    Model expected=new Model("model");
    GameFixture game;
    boolean justRun=true;
}
