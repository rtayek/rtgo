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
    @Before public void setUp() throws Exception { game=Game.setUpStandaloneLocalGame(IO.noPort); }
    @After public void tearDown() throws Exception { game.stop(); }
    @Ignore @Test public void testInit() throws InterruptedException { game.startGameThread(); game.checkStatus(); }
    // ignoring just to clean up th eoutput
    @Test public void test() throws InterruptedException {
        //File file=new File("serverGames/game1.sgf");
        //File file=new File("existingGame.sgf");
        File file=new File("existing9x9Game.sgf");
        assertNotNull(game);
        game.startPlayerBackends();
        Game.loadExistinGame(file,recorder,game);
        System.out.println(game.recorderFixture.backEnd.model);
        // how do we go to a particular position?
        // use finder and goto node
        // how do we let one person drive this?
        assertTrue(game.areBoardsEqual());
        Model whiteModel=game.whiteFixture.backEnd.model;
        assertTrue(game.areBoardsEqual());
        game.startGameThread(); // now it's ok to start game
        System.out.println(whiteModel);
        Point point=new Point(3,3);
        assertEquals(Stone.vacant,whiteModel.board().at(point));
        
        whiteModel.setRole(Role.playWhite);
        System.out.println(whiteModel.role());
        Move move=new model.Move.MoveImpl(Stone.black,point);
        System.out.println("move: "+move);
        MoveResult moveResult=whiteModel.move(move);
        System.out.println(moveResult);
        assertEquals(MoveResult.badRole,moveResult);
        System.out.println(whiteModel);
        
        
        whiteModel.setRole(Role.anything);
        System.out.println(whiteModel.role());
        /*Move*/ move=new model.Move.MoveImpl(Stone.black,point);
        System.out.println("move: "+move);
        /*MoveResult*/ moveResult=whiteModel.move(move);
        System.out.println(moveResult);
        assertEquals(MoveResult.legal,moveResult);
        System.out.println(whiteModel);
        
        /*Move*/ move=new model.Move.MoveImpl(Stone.white,point);
        System.out.println("move: "+move);
        /*MoveResult*/ moveResult=whiteModel.move(move);
        System.out.println(moveResult);
        assertEquals(MoveResult.occupied,moveResult);
        System.out.println(whiteModel);
        
        Logging.setLevels(Level.OFF);
    }
    GameFixture game;
    Model recorder=new Model();
}
