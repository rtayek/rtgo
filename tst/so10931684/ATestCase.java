package so10931684;
import utilities.MyTestWatcher;
import org.junit.*;
public class ATestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass()); //
    @Test public void test() {}
}

