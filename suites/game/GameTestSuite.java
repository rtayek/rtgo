package game;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import game.AbstractGameTestCase.*;
import utilities.MyTestWatcher;
@RunWith(Suite.class) @SuiteClasses({ //
    GameSocketTestCase.class, //
    GameDuplexTestCase.class, //
}) public class GameTestSuite {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    
}
