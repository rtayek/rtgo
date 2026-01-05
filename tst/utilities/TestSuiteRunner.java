package utilities;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.*;
public class TestSuiteRunner {
    // should have been found!
    public static class ATestCase {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
        @Test public void testOk() throws Exception { assertTrue("baz",true); }
        @Ignore @Test public void testFail() throws Exception { fail("foo"); }
        @Ignore @Test public void testError() throws Exception { throw new RuntimeException("bar"); }
    }
    static void runFromTestCases(Class<?>... classes) {
        Result result=JUnitCore.runClasses(classes);
        TestRunReporter.reportResult(result);
    }
    public static void runMethods() {
        Class<ATestCase> clazz=ATestCase.class;
        String method="testOk";
        Request request=Request.method(clazz,method);
        TestRunReporter.logRequest(request);
        JUnitCore junit=new JUnitCore();
        Result result=junit.run(request);
        TestRunReporter.reportResult(result);
    }
    public static void main(String[] args) {
        runFromTestCases(ATestCase.class);
        //runFromTestCases(BothSocketTestCase.class,BothDuplexTestCase.class);
        runMethods();
    }
}
