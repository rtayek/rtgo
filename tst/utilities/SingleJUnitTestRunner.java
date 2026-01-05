package utilities;
import io.Logging;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.*;
class SingleJUnitTestRunner {
    public static class TestCase {
        //@Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
        @Test public void test() throws Exception {
            //String[] arguments=new String[] {"utilities.SingleJUnitTestRunner$TestCase#test"};
            String[] arguments=new String[] {"equipment.ScreenCoordinatesTestCase#testAt"};
            SingleJUnitTestRunner.main(arguments);
        }
    }
    private static Result runTest(String arg) throws ClassNotFoundException {
        String[] classAndMethod=arg.split("#");
        Request request=Request.method(Class.forName(classAndMethod[0]),classAndMethod[1]);
        TestRunReporter.logRequest(request);
        Result result=null;
        result=new JUnitCore().run(request);
        return result;
    }
    public static void main(String... args) throws ClassNotFoundException {
        // how to test this
        if(args!=null&&args.length>0) {
            for(String arg:args) {
                Result result=runTest(arg);
                TestRunReporter.reportResult(result);
                assertTrue(result.wasSuccessful());
            }
        } else {
            Logging.mainLogger.info("usage: classNaame tesCaseClass#testMethod ...");
        }
    }
}
