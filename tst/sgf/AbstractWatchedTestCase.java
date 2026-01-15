package sgf;

import org.junit.Rule;
import utilities.MyTestWatcher;

public abstract class AbstractWatchedTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
}
