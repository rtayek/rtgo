package suitetest;
import utilities.MyTestWatcher;
import org.junit.*;
public class CTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass()); //
    @Test public void test() {}
}

