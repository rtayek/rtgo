package suitetest;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import utilities.MyTestWatcher;
@RunWith(Suite.class) @SuiteClasses({ATestCase.class,BTestCase.class,CTestCase.class}) public class AllTestsSuite { //
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
}
