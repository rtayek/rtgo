package utilities;

import org.junit.Rule;
import utilities.MyTestWatcher;
public abstract class SuiteSupport {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
}

