package game;
import static org.junit.Assert.assertNotNull;
import org.junit.*;
import controller.*;
import io.*;
import model.Model;
public class LadExistinGameTestCase {
    @Before public void setUp() throws Exception {
        game=Game.setUpStandaloneLocalGame(IO.noPort);
        game.startPlayerBackends();
    }
    @After public void tearDown() throws Exception { game.stop(); }
    @Test public void testInit() throws InterruptedException {
        if(game.doInit) { // turning this on made stuff work?
            Response initializeResponse=game.initializeGame();
            if(!initializeResponse.isOk()) Logging.mainLogger.warning("initialize game is not ok!");
        }
        Thread.sleep(10);
        System.out.println(game.blackFixture.backEnd.model);
        //game.startGame();
    }
    @Test public void test() throws InterruptedException {
        assertNotNull(game);
        Model blackModel=game.blackFixture.backEnd.model;
        System.out.println(blackModel);
        if(game.doInit) { // turning this on made stuff work?
            Response initializeResponse=game.initializeGame();
            if(!initializeResponse.isOk()) Logging.mainLogger.warning("initialize game is not ok!");
        }
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
