package game;
import utilities.MyTestWatcher;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.*;
import controller.*;
import io.*;
import server.NamedThreadGroup;
import server.NamedThreadGroup.NamedThread;
public class ManyGamesTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    class Players implements Runnable {
        Players(GameFixture game) { this.game=game; }
        public void start() { (namedThread=NamedThreadGroup.createNamedThread(game.id,this,"players")).start(); }
        @Override public void run() {
            try {
                GameFixture.playSillyGame(game,m);
            } catch(Throwable t) {
                failure=t;
            } finally {
                finished.incrementAndGet();
            }
        }
        final GameFixture game;
        NamedThread namedThread;
        volatile Throwable failure;
    }
    @Before public void setUp() throws Exception { //
        for(int i=0;i<n;i++) {
            game=Game.setUpStandaloneLocalGame(IOs.anyPort);
            game.startGameThread();
            games.add(game);
        }
    }
    @After public void tearDown() throws Exception {
        if(IOs.currentThreadIsTimeLimited()) {
            Logging.mainLogger.info("not main! "+"'"+Thread.currentThread().getName());
            Logging.mainLogger.severe("not main! "+"'"+Thread.currentThread().getName());
        }
        for(int i=0;i<n;i++) { games.get(i).stop(); }
    }
    @Test() public void testSomeSillyGames() throws Exception {
        List<Players> playersList=new ArrayList<>();
        for(GameFixture game:games) {
            Players players=new Players(game);
            playersList.add(players);
            players.start();
        }
        long timeoutNanos=30_000_000_000L;
        long t0=System.nanoTime();
        while(finished.get()<n) {
            if(System.nanoTime()-t0>timeoutNanos) fail("timed out waiting for players to finish");
            GTPBackEnd.sleep2(GTPBackEnd.yield);
        }
        for(Players players:playersList) {
            if(players.failure!=null) fail(players.failure.toString());
        }
    }
    final AtomicInteger finished=new AtomicInteger();
    GameFixture game;
    final int m=3;
    static final int n=3;
    final ArrayList<GameFixture> games=new ArrayList<>();
}

