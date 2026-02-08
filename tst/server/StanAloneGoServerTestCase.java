package server;
import utilities.MyTestWatcher;
import static controller.GameFixture.printStuff;
import static org.junit.Assert.*;
import java.util.Collection;
import com.tayek.util.core.ParameterArray;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import controller.*;
import equipment.*;
import io.*;
import model.*;
import model.Move2.MoveType;
import utilities.*;
import com.tayek.util.core.Histogram;
@RunWith(Parameterized.class) public class StanAloneGoServerTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass()); // standalone tests
    static final int n=1;
    @Parameters public static Collection<Object[]> data() { return ParameterArray.modulo(n); }
    // intermittent failure.
    // seems to work much better if we run one tests at a time.
    // may be a clue here?
    public StanAloneGoServerTestCase(Integer i) { this.i=i; }
    // these usually pass except for a few timeouts
    @Before public void setUp() throws Exception {
        //watchdog=Watchdog.watchdog(Thread.currentThread());
        // check for duplicate code in other tests.
        goServer=GoServer.startServer(i%2==0?IOs.anyPort:IOs.noPort);
        assertNotNull("no go server!",goServer);
        final int port=goServer.serverSocket!=null?goServer.serverSocket.getLocalPort():IOs.noPort;
        game=goServer.setupRemoteGameBackEnds(port);
        assertNotNull("no game from server!",game);
        GTPBackEnd.sleep2(2); // try to find out why this is necessary.
        Logging.mainLogger.info("waiting: "+game.recorderFixture.backEnd.isWaitingForMove());
        assertNotNull("black board",game.recorderFixture.backEnd.model.board());
        width=game.recorderFixture.backEnd.model.board().width();
        depth=game.recorderFixture.backEnd.model.board().depth();
    }
    @After public void tearDown() throws Exception {
        if(goServer!=null) goServer.stop();
        if(game!=null) game.stop();
        if(goServer!=null) GoServer.stop(null,game);
        if(watchdog!=null) watchdog.done=true;
    }
    @Test() public void testPlayZeroMoves() throws Exception {
        //printStuff(game);
        //game.blackFixture.backEnd.waitUntilItIsTmeToMove(); // was uncommented 1/5/23
        Logging.mainLogger.info("exit testPlayZeroMoves()");
    }
    @Test() public void testPlayOneMove() throws Exception {
        printStuff(game);
        playMoves(new Move2(MoveType.move,Stone.black,new Point()));
        Logging.mainLogger.info("exit testPlayOneMove()");
    }
    @Test() public void testPlayTwoMoves() throws Exception {
        playMoves(
                new Move2(MoveType.move,Stone.black,new Point()),
                new Move2(MoveType.move,Stone.white,new Point(0,1))
        );
    }
    @Test(/*timeout=1000/*timeoutTime*/) public void testPlaySomeMoves() throws Exception {
        int n=1; // will fail on small boards
        // zero for now just for a test
        for(int i=0;i<n;++i) {
            BothEnds player=i%2==0?game.blackFixture:game.whiteFixture;
            Stone color=game.color(player);
            playMoves(new Move2(MoveType.move,color,new Point(0,i)));
            assertTrue(game.blackFixture.backEnd.model.board().isEqual(game.whiteFixture.backEnd.model.board()));
        }
    }
    private void playMoves(Move2... moves) throws Exception {
        for(Move2 move:moves) {
            BothEnds from=move.color.equals(Stone.black)?game.blackFixture:game.whiteFixture;
            BothEnds to=from==game.blackFixture?game.whiteFixture:game.blackFixture;
            game.playOneMoveAndWait(from,to,move);
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


