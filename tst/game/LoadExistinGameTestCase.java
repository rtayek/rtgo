package game;
import static org.junit.Assert.*;
import org.junit.*;
import controller.GameFixture;
import io.IO;
import model.Model;
public class LoadExistinGameTestCase {
    @Before public void setUp() throws Exception {
        game=Game.setUpStandaloneLocalGame(IO.noPort);
    }
    @After public void tearDown() throws Exception { game.stop(); }
    @Test public void testInit() throws InterruptedException {
        game.startGameThread();
        game.checkStatus();
    }
    @Test public void test() throws InterruptedException {
        // started to fail after 1/05/22 when refactoring start game.
        // 1/6/23 fixed.
        // do not start game since it will prevent loading.
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
