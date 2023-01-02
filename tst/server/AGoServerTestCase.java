package server;
import static controller.GameFixture.printStuff;
import static org.junit.Assert.*;
import java.util.Collection;
import java.util.logging.Level;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import controller.*;
import equipment.*;
import io.*;
import model.Move;
import utilities.*;
@RunWith(Parameterized.class) public class AGoServerTestCase { // standalone tests
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    static final int n=5;
    @Parameters public static Collection<Object[]> data() { return ParameterArray.modulo(n); }
    // intermittent failure.
    // seems to work much better if we run one tests at a time.
    // may be a clue here?
    public AGoServerTestCase(Integer i) { this.i=i; }
    // these usually pass except for a few timeouts
    @Before public void setUp() throws Exception {
        System.out.println(Init.first+"enter setup");
        Logging.setLevels(Level.CONFIG);
        //watchdog=Watchdog.watchdog(Thread.currentThread());
        // check for duplicate code in other tests.
        goServer=GoServer.startServer(i%2==0?IO.anyPort:IO.noPort);
        assertNotNull("no go server!",goServer);
        final int port=goServer.serverSocket!=null?goServer.serverSocket.getLocalPort():IO.noPort;
        System.out.println("setup game on server");
        game=goServer.setUpGameOnServerAndWaitForAGame(port);
        assertNotNull("no game from server!",game);
        GTPBackEnd.sleep2(2); // try to find out why this is necessary.
        System.out.println("waiting: "+game.recorderFixture.backEnd.isWaitingForMove());
        assertNotNull("black board",game.recorderFixture.backEnd.model.board());
        width=game.recorderFixture.backEnd.model.board().width();
        depth=game.recorderFixture.backEnd.model.board().depth();
        System.out.println("exit setup");
    }
    @After public void tearDown() throws Exception {
        if(goServer!=null) goServer.stop();
        if(game!=null) game.stop();
        if(goServer!=null) GoServer.stop(null,game);
        if(watchdog!=null) watchdog.done=true;
    }
    @Test() public void testPlayZeroMoves() throws Exception {
        game.blackFixture.backEnd.waitUntilItIsTmeToMove();
        printStuff(game);
    }
    @Test() public void testPlayOneMove() throws Exception {
        printStuff(game);
        game.playOneMoveAndWait(game.blackFixture,game.whiteFixture,new Move.MoveImpl(Stone.black,new Point()));
    }
    @Test() public void testPlayTwoMoves() throws Exception {
        game.playOneMoveAndWait(game.blackFixture,game.whiteFixture,new Move.MoveImpl(Stone.black,new Point()));
        game.playOneMoveAndWait(game.whiteFixture,game.blackFixture,new Move.MoveImpl(Stone.white,new Point(0,1)));
    }
    @Test(/*timeout=1000/*timeoutTime*/) public void testPlaySomeMoves() throws Exception {
        int n=10; // will fail on small boards
        for(int i=0;i<n;++i) {
            BothEnds player=i%2==0?game.blackFixture:game.whiteFixture;
            Stone color=game.color(player);
            BothEnds opponent=game.opponent(player);
            game.playOneMoveAndWait(player,opponent,new Move.MoveImpl(color,new Point(0,i)));
            assertTrue(game.blackFixture.backEnd.model.board().isEqual(game.whiteFixture.backEnd.model.board()));
        }
    }
    int i;
    int width,depth;
    Watchdog watchdog;
    GoServer goServer;
    GameFixture game;
    int tSetup,tStartTest;
    Histogram hSetup=new Histogram();
    Histogram hStartTest=new Histogram();
}
