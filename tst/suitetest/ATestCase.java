package suitetest;
import static io.Init.first;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import io.Init;
import suitetest.ATestCase.Hide.AllTests2Suite;
import utilities.MyTestWatcher;
public class ATestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    public interface I {}
    static class Hide {
        @RunWith(Suite.class) @SuiteClasses({ATestCase.class,BTestCase.class,
                CTestCase.class}) public static class AllTests2Suite implements I {
            @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
            {
                System.out.println("suite");
            }
        }
    }
    // [{ExactMatcher:fDisplayName=main(java.lang.String[])], {LeadingIdentifierMatcher:fClassName=suitetest.ATestCase,fLeadingIdentifier=main]] from org.junit.internal.requests.ClassRequest@574caa3f
    @Test public void test() {}
    public static void main(String[] args) {
        System.out.println(Init.first);
        first.suiteControls=true;
        JUnitCore jUnitCore=new JUnitCore();
        jUnitCore.run(AllTests2Suite.class);
    }
}
