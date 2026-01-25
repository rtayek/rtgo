package utilities;

import org.junit.Rule;

public abstract class TestSupport {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
}
