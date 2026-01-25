package server;
import static org.junit.Assert.assertNotNull;
import org.junit.*;
import controller.*;
import equipment.*;
import io.*;
import io.IOs;
import model.*;
import model.Move2.MoveType;
import utilities.TestSupport;
public abstract class AbstractGoServerTestCase extends TestSupport {
    public static class GoServerRealSocketTestCase extends AbstractGoServerTestCase {
        @Override @Before public void setUp() throws Exception { serverPort=IOs.defaultPort; super.setUp(); }
        @Override @After public void tearDown() throws Exception { if(game!=null) game.stop(); super.tearDown(); }
    }
    public static class GoServerSocketTestCase extends AbstractGoServerTestCase {
        @Override @Before public void setUp() throws Exception { serverPort=IOs.anyPort; super.setUp(); }
        @Override @After public void tearDown() throws Exception { if(game!=null) game.stop(); super.tearDown(); }
    }
    public static class GoServerDuplexTestCase extends AbstractGoServerTestCase {
        // tests failing on interrupted exception
        @Override @Before public void setUp() throws Exception { serverPort=IOs.noPort; super.setUp(); }
        @Override @After public void tearDown() throws Exception { if(game!=null) game.stop(); super.tearDown(); }
    }
    @Before public void setUp() throws Exception {
        //watchdog=watchdog(Thread.currentThread());
        goServer=GoServer.startServer(serverPort);
    }
    @After public void tearDown() throws Exception {
        GoServer.stop(goServer,game);
        if(watchdog!=null) watchdog.done=true;
    }
    void check() {
        assertNotNull("no go server!",goServer);
        final int port=goServer.serverSocket!=null?goServer.serverSocket.getLocalPort():IOs.noPort;
        game=goServer.setupRemoteGameBackEnds(port); // and this waits for a game
        assertNotNull("no game from server!",game);
        GTPBackEnd.sleep2(1); // try to find out why this is necessary.
        Logging.mainLogger.info("waiting: "+game.recorderFixture.backEnd.isWaitingForMove());
        assertNotNull("black board",game.recorderFixture.backEnd.model.board());
        width=game.recorderFixture.backEnd.model.board().width();
        depth=game.recorderFixture.backEnd.model.board().depth();
    }
    @Test() public void testZeroeMovse() throws Exception { check(); }
    @Test() public void testPlayOneMove() throws Exception {
        check();
        playMoves(new Move2(MoveType.move,Stone.black,new Point()));
    }
    @Test() public void testPlayTwoMoves() throws Exception {
        check();
        playMoves(
                new Move2(MoveType.move,Stone.black,new Point()),
                new Move2(MoveType.move,Stone.white,new Point(1,0))
        );
    }
    @Test() public void testPlayThreeMoves() throws Exception {
        check();
        playMoves(
                new Move2(MoveType.move,Stone.black,new Point()),
                new Move2(MoveType.move,Stone.white,new Point(1,0)),
                new Move2(MoveType.move,Stone.black,new Point(2,0))
        );
    }
    @Test() public void testPassOnce() throws Exception {
        check();
        playMoves(Move2.blackPass);
    }
    @Test() public void testPassTwice() throws Exception {
        check();
        playMoves(Move2.blackPass,Move2.whitePass);
    }
    @Test() public void testResign() throws Exception {
        check();
        playMoves(Move2.blackResign);
    }
    protected final void playMoves(Move2... moves) throws Exception {
        for(Move2 move:moves) {
            BothEnds from=move.color.equals(Stone.black)?game.blackFixture:game.whiteFixture;
            BothEnds to=from==game.blackFixture?game.whiteFixture:game.blackFixture;
            game.playOneMoveAndWait(from,to,move);
        }
    }
    GoServer goServer;
    public Integer serverPort;
    public int width,depth;
    public GameFixture game;
    Watchdog watchdog;
}
