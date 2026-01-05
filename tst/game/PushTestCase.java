package game;
import io.Logging;
import static org.junit.Assert.*;
import org.junit.*;
import controller.GameFixture;
import model.*;
import model.ModelHelper2;
public class PushTestCase {
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testPushInGameDuplex() throws Exception { //1 asffadfpadsd[qsdk,q'sdq[
        game=Game.setupLocalGameForShove(expected);
        // 1/6/23
        // this is the only test that uses game
        // try to remove the dependency
        // and put a copy of this in tst/game
        Model black=game.blackFixture.backEnd.model;
        Model white=game.whiteFixture.backEnd.model;
        if(expected.board()!=null) { // normally no access to both of these at the same time
            black.setRoot(expected.board().width(),expected.board().depth());
            white.setRoot(expected.board().width(),expected.board().depth());
            // probably need to set other stuff like shape etc.
        } else Logging.mainLogger.info("expected has null board!");
        game.printStatus();
        assertTrue(game.namedThread==null);
        game.startPlayerBackends();
        ModelHelper2.getMovesAndPush(game.blackFixture.frontEnd,expected,true); // maybe all at once?
        ModelHelper2.getMovesAndPush(game.whiteFixture.frontEnd,expected,true);
        // GTPBackEnd.sleep(100); ???
        //assertTrue(recorder.board().isEqual(expected.board()));
        black.ensureBoard();
        white.ensureBoard();
        // the ensures make the test pass.
        // but there ate no moves!
        assertTrue(black.board().isEqual(expected.board()));
        assertTrue(white.board().isEqual(expected.board()));
    }
    Model expected=new Model("model");
    GameFixture game;
    boolean justRun=true;
}
