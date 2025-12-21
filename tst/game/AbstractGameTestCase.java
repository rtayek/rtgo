package game;
import java.util.Collection;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import controller.GameFixture;
import equipment.*;
import io.IOs;
import model.Move;
import utilities.*;
public abstract class AbstractGameTestCase { // these test cases require a running game.
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    public static class GameSocketTestCase extends AbstractGameTestCase {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
        @Override @Before public void setUp() throws Exception { serverPort=IOs.anyPort; super.setUp(); }
        @Override @After public void tearDown() throws Exception { game.stop(); super.tearDown(); }
        @RunWith(Parameterized.class) public static class ParameterizedGameSocketTestCase extends GameSocketTestCase {
            @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
            @Override @Before public void setUp() throws Exception { super.setUp(); }
            @Override @After public void tearDown() throws Exception { super.tearDown(); }
            public ParameterizedGameSocketTestCase(int i) { this.i=i; }
            @Parameters public static Collection<Object[]> data() { return ParameterArray.modulo(n); }
            final int i;
            static final int n=5;
        }
    }
    public static class GameDuplexTestCase extends AbstractGameTestCase {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
        @Override @Before public void setUp() throws Exception { serverPort=IOs.noPort; super.setUp(); }
        @Override @After public void tearDown() throws Exception { game.stop(); super.tearDown(); }
        @RunWith(Parameterized.class) public static class ParameterizedGameDuplexTestCase extends GameDuplexTestCase {
            @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
            @Override @Before public void setUp() throws Exception { super.setUp(); }
            @Override @After public void tearDown() throws Exception { super.tearDown(); }
            public ParameterizedGameDuplexTestCase(int i) { this.i=i; }
            @Parameters public static Collection<Object[]> data() { return ParameterArray.modulo(n); }
            final int i;
            static final int n=2;
        }
    }
    @Before public void setUp() throws Exception {
        game=Game.setUpStandaloneLocalGame(serverPort);
        game.startGameThread();
        game.checkStatus();
    }
    @After public void tearDown() throws Exception { game.stop(); }
    // these guys need a game running.
    @Test() public void testPlayZeroMoves() throws Exception {}
    @Test() public void testPlayOneMove() throws Exception {
        game.playOneMoveAndWait(game.blackFixture,game.whiteFixture,new Move.MoveImpl(Stone.black,new Point()));
    }
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
    @Test() public void testPassThreTimes() throws Exception {
        game.playOneMoveAndWait(game.blackFixture,game.whiteFixture,Move.blackPass);
        game.playOneMoveAndWait(game.whiteFixture,game.blackFixture,Move.whitePass);
        game.playOneMoveAndWait(game.blackFixture,game.whiteFixture,Move.blackPass);
    }
    @Test() public void testResign() throws Exception {
        game.playOneMoveAndWait(game.blackFixture,game.whiteFixture,Move.blackResign);
    }
    public GameFixture game;
    public Integer serverPort;
    public int width,depth;
    static final int timeout=0; //GTPBackEnd.timeoutTime;
}
