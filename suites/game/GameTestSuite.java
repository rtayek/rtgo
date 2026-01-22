package game;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import game.AbstractGameTestCase.*;
import utilities.SuiteSupport;
@RunWith(Suite.class)
@SuiteClasses({ //
    GameSocketTestCase.class, //
    GameDuplexTestCase.class, //
})
public class GameTestSuite extends SuiteSupport {
}
