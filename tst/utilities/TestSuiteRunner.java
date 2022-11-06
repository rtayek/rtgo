package utilities;
import static org.junit.Assert.*;
import java.util.List;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runner.notification.Failure;
public class TestSuiteRunner {
    // should have been found!
    public static class ATestCase {
        @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
        @Test public void testOk() throws Exception { assertTrue("baz",true); }
        @Test public void testFail() throws Exception { fail("foo"); }
        @Test public void testError() throws Exception { throw new RuntimeException("bar"); }
    }
    public static void printFailures(Result result) {
        List<Failure> failures=result.getFailures();
        if(failures.size()>0) {
            System.out.println(failures.size()+" failures: "+failures);
            for(Failure failure:result.getFailures()) {
                System.out.println("message: "+failure.getMessage());
                if(failure.getException()!=null) {
                    System.out.println(failure.getException());
                    //failure.getException().printStackTrace();
                }
            }
        } else System.out.println("no failures or errors.");
    }
    static void runFromTestCases(Class<?>... classes) {
        Result result=JUnitCore.runClasses(classes);
        System.out.println("after run");
        printFailures(result);
        System.out.println("Test successful? "+result.wasSuccessful());
    }
    public static void runMethods() {
        Class<ATestCase> clazz=ATestCase.class;
        String method="testOk";
        System.out.println("get request");
        Request request=Request.method(clazz,method);
        System.out.println(request);
        JUnitCore junit=new JUnitCore();
        Result result=junit.run(request);
        System.out.println("after run");
        printFailures(result);
        System.out.println("Test successful? "+result.wasSuccessful());
    }
    public static void main(String[] args) {
        runFromTestCases(ATestCase.class);
        //runFromTestCases(BothSocketTestCase.class,BothDuplexTestCase.class);
        runMethods();
    }
}