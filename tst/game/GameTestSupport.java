package game;

import controller.GameFixture;
import io.IOs;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import utilities.MyTestWatcher;

public abstract class GameTestSupport {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    protected GameFixture game;
    protected Integer serverPort = IOs.anyPort;
    protected static final int timeout = 0;

    @Before public void setUp() throws Exception {
        if (serverPort == null) serverPort = IOs.anyPort;
        game = Game.setUpStandaloneLocalGame(serverPort);
        game.startGameThread();
        game.checkStatus();
    }

    @After public void tearDown() throws Exception {
        if (game != null) {
            game.stop();
            game = null;
        }
    }

    protected void setServerPort(Integer port) { serverPort = port; }
}
