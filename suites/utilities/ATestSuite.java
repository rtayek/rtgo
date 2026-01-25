package utilities;
import static org.junit.Assert.assertTrue;
// move this to suites/
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
@RunWith(Suite.class)
@SuiteClasses({ATestSuite.A1TestCase.class,ATestSuite.A2TestCase.class,})
public class ATestSuite extends SuiteSupport {
    public static class A1TestCase extends SuiteSupport {
        @Test public void test1() { assertTrue(true); }
    }
    public static class A2TestCase extends SuiteSupport {
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
