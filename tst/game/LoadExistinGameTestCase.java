package game;
import static org.junit.Assert.*;
import java.util.logging.Level;
import org.junit.*;
import controller.GameFixture;
import io.*;
import model.Model;
public class LoadExistinGameTestCase {
    @Before public void setUp() throws Exception {
        System.out.println(Init.first);
        Logging.setLevels(Level.CONFIG);
        game=Game.setUpStandaloneLocalGame(IO.noPort);
    }
    @After public void tearDown() throws Exception { game.stop(); }
    @Test public void testInit() throws InterruptedException {
        game.startGameThread();
        // test something!
    }
    @Test public void test() throws InterruptedException {
        // started to fail after 1/05/22 when refactoring start game.
        assertNotNull(game);
        game.startPlayerBackends();
        assertTrue("game already started",game.namedThread==null);
        Model blackModel=game.blackFixture.backEnd.model;
        Game.loadExistinGame(recorder,game);
        Thread.sleep(10);
        System.out.println(blackModel);
        blackModel.down(0);
        Thread.sleep(10);
        System.out.println(blackModel);
        blackModel.down(0);
        Thread.sleep(10);
        System.out.println(blackModel);
        //game.startGame();
    }
    GameFixture game;
    Model recorder=new Model();
}
