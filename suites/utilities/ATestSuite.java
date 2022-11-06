package utilities;
import static org.junit.Assert.assertTrue;
// move this to suites/
import org.junit.*;
import org.junit.internal.TextListener;
import org.junit.runner.*;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import utilities.ATestSuite.*;
@RunWith(Suite.class) @SuiteClasses({A1TestCase.class,A2TestCase.class,}) public class ATestSuite {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());;
    public static class A1TestCase {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
                @Test public void test1() { assertTrue(true); }
    }
    public static class A2TestCase {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
                @Test public void test2() { assertTrue(true); }
    }
    public static void main(String[] args) { // should work both ways
        boolean suite=true;
        if(!suite) {
            JUnitCore junit=new JUnitCore();
            junit.addListener(new TextListener(System.out));
            junit.run(A1TestCase.class);
        } else {
            JUnitCore.runClasses(ATestSuite.class);
        }
    }
}
