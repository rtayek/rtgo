package utilities;

import org.junit.Rule;
import utilities.MyTestWatcher;

public abstract class TestSupport {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
}
