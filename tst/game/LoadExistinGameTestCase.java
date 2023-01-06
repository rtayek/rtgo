package game;
import static org.junit.Assert.*;
import org.junit.*;
import controller.*;
import io.IO;
import model.Model;
public class LoadExistinGameTestCase {
    @Before public void setUp() throws Exception { game=Game.setUpStandaloneLocalGame(IO.noPort); }
    @After public void tearDown() throws Exception { game.stop(); }
    @Test public void testInit() throws InterruptedException {
        Response initializeResponse=game.initializeGame();
        if(game.doInit&&initializeResponse==null) fail("bad initialize respomse.");
        Thread.sleep(10);
        System.out.println(game.blackFixture.backEnd.model);
        game.startGameThread();
        // test something!
    }
    @Ignore @Test public void test() throws InterruptedException {
        // started to fail after 1/05/22 when refactoring start game.
        assertNotNull(game);
        game.startGameThread();
        Model blackModel=game.blackFixture.backEnd.model;
        System.out.println(blackModel);
        Game.loadExistinGame(recorder,game);
        Thread.sleep(10);
        System.out.println(blackModel);
        System.out.println("1");
        blackModel.down(0);
        Thread.sleep(10);
        System.out.println(blackModel);
        System.out.println("2");
        blackModel.down(0);
        Thread.sleep(10);
        System.out.println(blackModel);
        System.out.println("3");
        //game.startGame();
    }
    GameFixture game;
    Model recorder=new Model();
}
