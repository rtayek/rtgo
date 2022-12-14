package sgf;
import org.junit.Rule;
import utilities.MyTestWatcher;
public abstract class AbstractMultipleGamesTestCase extends AbstractMNodeRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
}
