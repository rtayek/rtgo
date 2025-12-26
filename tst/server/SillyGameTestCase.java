package server;
import org.junit.*;
import controller.GameFixture;
import io.*;
import io.IOs;
import utilities.MyTestWatcher;
public class SillyGameTestCase extends AbstractGoServerTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception {
        Logging.mainLogger.info(this+" starting setup.");
        serverPort=IOs.anyPort;
        super.setUp();
        Logging.mainLogger.info(this+" exiting setup.");
    }
    @Override @After public void tearDown() throws Exception { super.tearDown(); }
    @Test() public void testPlaySillyGame() throws Exception {
        // torus would make make a good test case
        // this has a timeout
        int moves=width*depth-1;
        moves/=10;
        check();
        GameFixture.playSillyGame(game,moves);
    }
    Thread target;
}
