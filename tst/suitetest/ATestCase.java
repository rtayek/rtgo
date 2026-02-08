package suitetest;
import utilities.MyTestWatcher;
import io.Logging;
import static io.Init.first;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import io.Init;
import suitetest.ATestCase.Hide.AllTests2Suite;
public class ATestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    public interface I {}
    static class Hide {
        @RunWith(Suite.class) @SuiteClasses({ATestCase.class,BTestCase.class,
                CTestCase.class}) public static class AllTests2Suite implements I {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
            {
                Logging.mainLogger.info("suite");
            }
        }
    }
    // [{ExactMatcher:fDisplayName=main(java.lang.String[])], {LeadingIdentifierMatcher:fClassName=suitetest.ATestCase,fLeadingIdentifier=main]] from org.junit.internal.requests.ClassRequest@574caa3f
    @Test public void test() {}
    public static void main(String[] args) {
        Logging.mainLogger.info(String.valueOf(Init.first));
        first.suiteControls=true;
        JUnitCore jUnitCore=new JUnitCore();
        jUnitCore.run(AllTests2Suite.class);
    }
}


