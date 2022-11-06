package suitetest;
import org.junit.*;
import utilities.MyTestWatcher;
public class CTestCase { //
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void test() {}
}
