package suitetest;
import org.junit.Rule;
import utilities.MyTestWatcher;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class) @SuiteClasses({ATestCase.class,BTestCase.class,CTestCase.class}) public class AllTestsSuite {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass()); //
}


