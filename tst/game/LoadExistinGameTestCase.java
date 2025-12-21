package game;
import static org.junit.Assert.*;
import java.io.File;
import java.util.logging.Level;
import org.junit.*;
import controller.GameFixture;
import equipment.*;
import io.*;
import model.*;
import model.Model.*;
public class LoadExistinGameTestCase {
    // how do we go to a particular position?
    // use finder and goto node
    // how do we let one person drive this?
    @Before public void setUp() throws Exception {
        game=Game.setUpStandaloneLocalGame(IOs.noPort);
        black=game.blackFixture.backEnd.model;
        white=game.whiteFixture.backEnd.model;
    }
    @After public void tearDown() throws Exception { game.stop(); }
    @Ignore @Test public void testInit() throws InterruptedException { game.startGameThread(); game.checkStatus(); }
    // ignoring just to clean up th eoutput
    void restoreGame() {
        assertNotNull(game);
        game.startPlayerBackends();
        Game.loadExistinGame(file,recorder,game);
        assertTrue(game.areBoardsEqual());
        game.startGameThread();
        System.out.println(recorder.role()+" "+black.role()+" "+white.role());
    }
    @Test public void testLoadGame() throws InterruptedException { restoreGame(); }
    @Test public void test() throws InterruptedException {
        restoreGame();
        // the rest of these cases have been moved to role test case.
        assertEquals(Stone.vacant,white.board().at(point));
        white.setRole(Role.playWhite);
        move=new model.Move.MoveImpl(Stone.black,point);
        moveResult=white.move(move);
        assertEquals(MoveResult.badRole,moveResult);
        white.setRole(Role.anything);
        move=new model.Move.MoveImpl(Stone.black,point);
        moveResult=white.move(move);
        assertEquals(MoveResult.legal,moveResult);
        System.out.println(white);
        move=new model.Move.MoveImpl(Stone.white,point);
        moveResult=white.move(move);
        assertEquals(MoveResult.occupied,moveResult);
        Logging.setLevels(Level.OFF);
    }
    Point point=new Point(3,3);
    Move move;
    MoveResult moveResult;
    GameFixture game;
    Model recorder=new Model(),black,white;
    File file=new File("existing9x9Game.sgf");
}
