package game;
import java.util.Collection;
import java.util.logging.Level;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import controller.*;
import io.*;
import utilities.*;
@RunWith(Parameterized.class) public class BasicGameTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> data() { return ParameterArray.modulo(n); }
    public BasicGameTestCase(int i) { this.i=i; }
    @Before public void setUp() throws Exception {
        System.out.println(Init.first);
        Logging.setLevels(Level.CONFIG);
        game=Game.setUpStandaloneLocalGame(IO.anyPort);
    }
    @After public void tearDown() throws Exception {
        if(IO.currentThreadIsTimeLimited()) {
            System.out.println("not main! "+"'"+Thread.currentThread().getName());
            Logging.mainLogger.severe("not main! "+"'"+Thread.currentThread().getName());
        }
        game.stop();
    }
    @Test() public void testillyGame() throws Exception {
        game.startPlayerBackends();
        if(game.doInit) { // turning this on made stuff work?
            Response initializeResponse=game.initializeGame();
            if(!initializeResponse.isOk()) Logging.mainLogger.warning("initialize game is not ok!");
        }
        game.startGameThread();
        et.reset();
        GameFixture.playSillyGame(game,m);
    }
    int i;
    Et et=new Et();
    GameFixture game;
    final int m=3;
    static final int n=1;
}
