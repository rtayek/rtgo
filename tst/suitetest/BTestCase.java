package suitetest;
import org.junit.*;
import utilities.MyTestWatcher;
public class BTestCase { //
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void test() {}
}
