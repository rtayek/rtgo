package server;
import org.junit.*;
import equipment.*;
import io.IO;
import model.Move;
import utilities.MyTestWatcher;
public class GoServerTestCase extends AbstractGoServerTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    // some tests time out when other jvms are running!
    // still sorta fragile
    // some of this may go away now that we have two flavours of abstract go server test case
    @Override @Before public void setUp() throws Exception { serverPort=IO.anyPort; super.setUp(); }
    @Override @After public void tearDown() throws Exception { super.tearDown(); }
    @Test() public void testPlayTwoMoves() throws Exception {
        game.playOneMoveAndWait(game.blackFixture,game.whiteFixture,new Move.MoveImpl(Stone.black,new Point()));
        game.playOneMoveAndWait(game.whiteFixture,game.blackFixture,new Move.MoveImpl(Stone.white,new Point(1,0)));
    }
    @Test() public void testPlayThreeMoves() throws Exception {
        game.playOneMoveAndWait(game.blackFixture,game.whiteFixture,new Move.MoveImpl(Stone.black,new Point()));
        game.playOneMoveAndWait(game.whiteFixture,game.blackFixture,new Move.MoveImpl(Stone.white,new Point(1,0)));
        game.playOneMoveAndWait(game.blackFixture,game.whiteFixture,new Move.MoveImpl(Stone.black,new Point(2,0)));
    }
    @Test() public void testPassOnce() throws Exception {
        game.playOneMoveAndWait(game.blackFixture,game.whiteFixture,Move.blackPass);
    }
    @Test() public void testPassTwice() throws Exception {
        game.playOneMoveAndWait(game.blackFixture,game.whiteFixture,Move.blackPass);
        game.playOneMoveAndWait(game.whiteFixture,game.blackFixture,Move.whitePass);
    }
    @Test() public void testResign() throws Exception {
        game.playOneMoveAndWait(game.blackFixture,game.whiteFixture,Move.blackResign);
    }
    //Model blackModel,whiteModel;
}
