package game;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import org.junit.*;
import controller.*;
import io.*;
import server.*;
import server.NamedThreadGroup.NamedThread;
import utilities.MyTestWatcher;
public class ManyGamesTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    class Players implements Runnable {
        Players(GameFixture game) { this.game=game; }
        public void start() { (namedThread=NamedThreadGroup.createNamedThread(game.id,this,"players")).start(); }
        @Override public void run() {
            try {
                GameFixture.playSillyGame(game,m);
            } catch(InterruptedException e) {
                e.printStackTrace();
                fail(e.toString());
                //throw new RuntimeException(e);
            }
            ++finished;
        }
        final GameFixture game;
        NamedThread namedThread;
    }
    @Before public void setUp() throws Exception { //
        for(int i=0;i<n;i++) {
            game=GoServer.setUpStandaloneGame(IO.anyPort);
            game.startPlayerBackends();
            games.add(game);
        }
    }
    @After public void tearDown() throws Exception {
        if(IO.currentThreadIsTimeLimited()) {
            System.out.println("not main! "+"'"+Thread.currentThread().getName());
            Logging.mainLogger.severe("not main! "+"'"+Thread.currentThread().getName());
        }
        for(int i=0;i<n;i++) { games.get(i).stop(); }
    }
    @Test() public void testSomeSillyGames() throws Exception {
        for(GameFixture game:games) {
            Players players=new Players(game);
            players.start();
        }
        //fail("will hang on wait");
        while(finished<n) { GTPBackEnd.sleep2(GTPBackEnd.yield); }
    }
    int finished;
    GameFixture game;
    final int m=3;
    static final int n=3;
    final ArrayList<GameFixture> games=new ArrayList<>();
}