package suitetest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import utilities.TestSupport;
@RunWith(Suite.class) @SuiteClasses({ATestCase.class,BTestCase.class,CTestCase.class}) public class AllTestsSuite extends TestSupport { //
}
