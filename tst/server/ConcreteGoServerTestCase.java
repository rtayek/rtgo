package server;
import org.junit.*;
import org.junit.runner.*;
import io.IO;
import utilities.MyTestWatcher;
// tries to runb a test programmatically from main();
public class ConcreteGoServerTestCase extends AbstractGoServerTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception {
        System.out.println("setup");
        serverPort=IO.anyPort;
        super.setUp();
    }
    @Override @After public void tearDown() throws Exception { System.out.println("teardown"); super.tearDown(); }
    public static void main(String[] args) throws Exception {
        // run a few test methods
        JUnitCore junit=new JUnitCore();
        Class<ConcreteGoServerTestCase> clazz=ConcreteGoServerTestCase.class;
        String method="test";
        System.out.println("get request");
        Request request=Request.method(clazz,method);
        System.out.println(request);
        String method3="adasdsd";
        System.out.println("get request");
        Request request3=Request.method(clazz,method3);
        System.out.println(request3);
        Result result=junit.run(request);
        System.out.println("result: "+result);
        System.out.println("was successful: "+result.wasSuccessful());
        Class<ConcreteGoServerTestCase> clazz2=ConcreteGoServerTestCase.class;
        String method2="testPlayOneMove";
        Request request2=Request.method(clazz2,method2);
        Result result2=junit.run(request2);
        System.out.println(method2+" "+result2.wasSuccessful());
        NamedThreadGroup.stopAllStopables();
        System.out.println(NamedThreadGroup.printNamedThreadGroups(true));
        Thread.sleep(100);
        System.out.println("|||");
        System.out.println(NamedThreadGroup.printNamedThreadGroups(true));
        System.out.println("exit");
        NamedThreadGroup.printThraedsAtEnd();
    }
}
