package game;
import static org.junit.Assert.*;
import java.io.File;
import java.util.logging.Level;
import org.junit.*;
import controller.*;
import controller.Command;
import equipment.*;
import io.*;
import model.*;
import model.Model.MoveResult;
public class LoadExistinGameTestCase {
    @Before public void setUp() throws Exception { game=Game.setUpStandaloneLocalGame(IO.noPort); }
    @After public void tearDown() throws Exception { game.stop(); }
    @Test public void testInit() throws InterruptedException { game.startGameThread(); game.checkStatus(); }
    @Test public void test() throws InterruptedException {
        assertNotNull(game);
        game.startPlayerBackends();
        assertTrue("game already started",game.namedThread==null);
        Model blackModel=game.blackFixture.backEnd.model;
        //File file=new File("serverGames/game1.sgf");
        File file=new File("existingGame.sgf");
        Game.loadExistinGame(file,recorder,game);
        // go to the end of the main line!
        game.bottom();
        // how do we go to a particular position?
        // use finder and goto node
        // how do we let one person drive this?
        assertTrue(game.areBoardsEqual());
        Logging.setLevels(Level.ALL);
        //blackModel.strict=true;
        Model whiteModel=game.whiteFixture.backEnd.model;
        System.out.println("before: "+whiteModel.role());
        String whiteCommand=Command.tgo_white.name();
        Response response=game.recorderFixture.frontEnd.sendAndReceive(whiteCommand);
        if(!response.isOk()) Logging.mainLogger.warning(whiteCommand+" fails!");
        Thread.sleep(100);
        System.out.println("after: "+whiteModel.role());
        game.startGameThread(); // now it's ok to start game
        Point point=new Point(4,4);
        // seems to allow duplicate move!
        Move move=new model.Move.MoveImpl(Stone.black,point);
        MoveResult moveResult=whiteModel.move(move);
        assertEquals(moveResult,MoveResult.legal);
        System.out.println(blackModel);
        System.out.println(blackModel.root());
        Logging.setLevels(Level.OFF);
    }
    GameFixture game;
    Model recorder=new Model();
}
