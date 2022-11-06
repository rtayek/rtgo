package utilities;
import java.util.List;
import org.junit.runner.*;
import org.junit.runner.notification.Failure;
import controller.AbstractGameFixtureTestCase.SocketTestCase;
public class RunOneTestMethod {
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
    public static void runMethods() {
        Class<SocketTestCase> clazz=SocketTestCase.class;
        System.out.println(clazz);
        String method="testA1";
        System.out.println("get request");
        Request request=Request.method(clazz,method);
        System.out.println(request);
        JUnitCore junit=new JUnitCore();
        Result result=junit.run(request);
        System.out.println("after run");
        printFailures(result);
        System.out.println("Test successful? "+result.wasSuccessful());
    }
    public static void main(String[] args) { runMethods(); }
}