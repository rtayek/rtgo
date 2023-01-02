package server;
import static org.junit.Assert.assertNotNull;
import org.junit.*;
import controller.*;
import equipment.*;
import io.*;
import model.Move;
import utilities.MyTestWatcher;
public abstract class AbstractGoServerTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    public static class GoServerRealSocketTestCase extends AbstractGoServerTestCase {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
        @Override @Before public void setUp() throws Exception { serverPort=IO.defaultPort; super.setUp(); }
        @Override @After public void tearDown() throws Exception { if(game!=null) game.stop(); super.tearDown(); }
    }
    public static class GoServerSocketTestCase extends AbstractGoServerTestCase {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
        @Override @Before public void setUp() throws Exception { serverPort=IO.anyPort; super.setUp(); }
        @Override @After public void tearDown() throws Exception { if(game!=null) game.stop(); super.tearDown(); }
    }
    public static class GoServerDuplexTestCase extends AbstractGoServerTestCase {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
        // tests failing on interrupted exception
        @Override @Before public void setUp() throws Exception { serverPort=IO.noPort; super.setUp(); }
        @Override @After public void tearDown() throws Exception { if(game!=null) game.stop(); super.tearDown(); }
    }
    @Before public void setUp() throws Exception {
        //watchdog=watchdog(Thread.currentThread());
        goServer=GoServer.startServer(serverPort);
        assertNotNull("no go server!",goServer);
        final int port=goServer.serverSocket!=null?goServer.serverSocket.getLocalPort():IO.noPort;
        game=goServer.setUpGameOnServerAndWaitForAGame(port); // and this waits for a game
        assertNotNull("no game from server!",game);
        GTPBackEnd.sleep2(1); // try to find out why this is necessary.
        System.out.println("waiting: "+game.recorderFixture.backEnd.isWaitingForMove());
        assertNotNull("black board",game.recorderFixture.backEnd.model.board());
        width=game.recorderFixture.backEnd.model.board().width();
        depth=game.recorderFixture.backEnd.model.board().depth();
    }
    @After public void tearDown() throws Exception {
        GoServer.stop(goServer,game);
        if(watchdog!=null) watchdog.done=true;
    }
    @Test() public void testZeroeMovse() throws Exception {}
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
    @Test() public void testResign() throws Exception {
        game.playOneMoveAndWait(game.blackFixture,game.whiteFixture,Move.blackResign);
    }
    GoServer goServer;
    public Integer serverPort;
    public int width,depth;
    public GameFixture game;
    Watchdog watchdog;
}
