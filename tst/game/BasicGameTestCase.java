package game;
import java.util.Collection;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import controller.GameFixture;
import io.*;
import io.IOs; 
import utilities.*;
@RunWith(Parameterized.class) public class BasicGameTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> data() { return ParameterArray.modulo(n); }
    public BasicGameTestCase(int i) { this.i=i; }
    @Before public void setUp() throws Exception {
        game=Game.setUpStandaloneLocalGame(IOs.anyPort);
        game.startGameThread();
    }
    @After public void tearDown() throws Exception {
        if(IOs.currentThreadIsTimeLimited()) {
            System.out.println("not main! "+"'"+Thread.currentThread().getName());
            Logging.mainLogger.severe("not main! "+"'"+Thread.currentThread().getName());
        }
        game.stop();
    }
    @Test() public void testillyGame() throws Exception {
        et.reset();
        GameFixture.playSillyGame(game,m);
    }
    int i;
    Et et=new Et();
    GameFixture game;
    final int m=3;
    static final int n=1;
}
