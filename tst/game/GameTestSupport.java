package game;

import org.junit.Rule;
import utilities.MyTestWatcher;
import controller.GameFixture;
import io.IOs;
import org.junit.After;
import org.junit.Before;
public abstract class GameTestSupport {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    protected GameFixture game;
    protected Integer serverPort = IOs.anyPort;
    protected static final int timeout = 0;
    protected boolean startGameThreadInSetUp = true;

    @Before public void setUp() throws Exception {
        if (serverPort == null) serverPort = IOs.anyPort;
        game = Game.setUpStandaloneLocalGame(serverPort);
        if (startGameThreadInSetUp) {
            startGameThreadNow();
        }
    }

    @After public void tearDown() throws Exception {
        if (game != null) {
            game.stop();
            game = null;
        }
    }

    protected void startGameThreadNow() {
        if (game != null && game.namedThread == null) {
            game.startGameThread();
            game.checkStatus();
        }
    }

    protected void setServerPort(Integer port) { serverPort = port; }
}

