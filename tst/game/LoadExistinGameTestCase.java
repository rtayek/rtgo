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
        game=Game.setUpStandaloneLocalGame(IO.noPort);
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
    @Test public void testMakeLegalBlackMove() throws InterruptedException {
        restoreGame();
        move=new model.Move.MoveImpl(Stone.black,point);
        if(!black.check(black.role(),Action.move)) throw new RuntimeException("check fails!");
        moveResult=black.move(move);
        assertEquals(black.board().at(point),Stone.black);
        assertEquals(MoveResult.legal,moveResult);
    }
    @Test public void testMakeMoveOutOfTurn() throws InterruptedException {
        restoreGame();
        move=new model.Move.MoveImpl(Stone.white,point);
        black.setRole(Role.playBlack);
        boolean ok=black.check(black.role(),Action.move);
        System.out.println("ok: "+ok);
        if(!ok) throw new RuntimeException("check fails!");
        System.out.println(black.turn());
        System.out.println(Role.playBlack.stone);
        System.out.println(black.turn().equals(black.role().stone));
        moveResult=black.move(move);
        assertEquals(MoveResult.notYourTurn,moveResult);
        // this returns not your turn
        // why doesn't check fail?
    }
    @Test public void testMakeMoveOutOfTurnAnything() throws InterruptedException {
        restoreGame();
        move=new model.Move.MoveImpl(Stone.white,point);
        if(!black.check(black.role(),Action.move)) throw new RuntimeException("check fails!");
        moveResult=black.move(move);
        assertEquals(MoveResult.legal,moveResult);
        assertEquals(black.board().at(point),Stone.white);
    }
    @Test public void testMoveOnOccupiedPoint() throws InterruptedException {
        restoreGame();
        move=new model.Move.MoveImpl(Stone.black,point);
        moveResult=black.move(move);
        assertEquals(black.board().at(point),Stone.black);
        assertEquals(MoveResult.legal,moveResult);
        move=new model.Move.MoveImpl(Stone.white,point);
        moveResult=black.move(move);
        assertEquals(MoveResult.occupied,moveResult);
    }
    @Test public void test() throws InterruptedException {
        restoreGame();
        System.out.println(white);
        assertEquals(Stone.vacant,white.board().at(point));
        white.setRole(Role.playWhite);
        System.out.println(white.role());
        move=new model.Move.MoveImpl(Stone.black,point);
        System.out.println("move: "+move);
        moveResult=white.move(move);
        System.out.println(moveResult);
        assertEquals(MoveResult.badRole,moveResult);
        System.out.println(white);
        white.setRole(Role.anything);
        System.out.println(white.role());
        move=new model.Move.MoveImpl(Stone.black,point);
        System.out.println("move: "+move);
        moveResult=white.move(move);
        System.out.println(moveResult);
        assertEquals(MoveResult.legal,moveResult);
        System.out.println(white);
        move=new model.Move.MoveImpl(Stone.white,point);
        System.out.println("move: "+move);
        moveResult=white.move(move);
        System.out.println(moveResult);
        assertEquals(MoveResult.occupied,moveResult);
        System.out.println(white);
        Logging.setLevels(Level.OFF);
    }
    Point point=new Point(3,3);
    Move move;
    MoveResult moveResult;
    GameFixture game;
    Model recorder=new Model(),black,white;
    File file=new File("existing9x9Game.sgf");
}
