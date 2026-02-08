package suitetest;
import utilities.MyTestWatcher;
import org.junit.*;
public class BTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass()); //
    @Test public void test() {}
}

