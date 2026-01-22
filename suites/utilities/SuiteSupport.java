package utilities;

import org.junit.Rule;

public abstract class SuiteSupport {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
}
