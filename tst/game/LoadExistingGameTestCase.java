package game;
import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import org.junit.*;
import equipment.*;
import io.*;
import io.IOs;
import model.*;
import model.Model.*;
import model.Move2.MoveType;
public class LoadExistingGameTestCase extends GameTestSupport {
	// how do we go to a particular position?
	// use finder and goto node
	// how do we let one person drive this?
	@Override @Before public void setUp() throws Exception {
		setServerPort(IOs.noPort);
		startGameThreadInSetUp=false;
		super.setUp();
		black=game.blackFixture.backEnd.model;
		white=game.whiteFixture.backEnd.model;
	}
	/*@Ignore*/ @Test public void testStatus() throws InterruptedException {
		game.startGameThread();
		game.checkStatus();
	}
	// ignoring just to clean up the output
	void loadGameFixture() throws IOException {
		assertNotNull(game);
		game.startPlayerBackends();
		Game.loadExistinGame(file,recorder,game);
		assertTrue(game.areBoardsEqual());
		startGameThreadNow();
		Logging.mainLogger.info(recorder.role()+" "+black.role()+" "+white.role());
	}
	@Test public void testLoadGame() throws InterruptedException,IOException {
		loadGameFixture();
	}
	@Test public void test() throws InterruptedException,IOException {
		loadGameFixture();
		// the rest of these cases have been moved to role test case.
		assertEquals(Stone.vacant,white.board().at(point));
		white.setRole(Role.playWhite);
		move=new Move2(MoveType.move,Stone.black,point);
		moveResult=white.move(move);
		assertEquals(MoveResult.badRole,moveResult);
		white.setRole(Role.anything);
		move=new Move2(MoveType.move,Stone.black,point);
		moveResult=white.move(move);
		assertEquals(MoveResult.legal,moveResult);
		Logging.mainLogger.info(String.valueOf(white));
		move=new Move2(MoveType.move,Stone.white,point);
		moveResult=white.move(move);
		assertEquals(MoveResult.occupied,moveResult);
		Logging.setLevels(Level.OFF);
	}
	Point point=new Point(3,3);
	Move2 move;
	MoveResult moveResult;
	Model recorder=new Model(),black,white;
	File file=new File("data/wasinroot/existing9x9Game.sgf");
}
