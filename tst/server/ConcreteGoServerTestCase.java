package server;
import org.junit.*;
import org.junit.runner.*;
import io.IO;
import utilities.MyTestWatcher;
// tries to runb a test programmatically from main();
public class ConcreteGoServerTestCase extends AbstractGoServerTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception {
        serverPort=IO.anyPort;
        super.setUp();
    }
    @Override @After public void tearDown() throws Exception { super.tearDown(); }
    public static void main(String[] args) throws Exception {
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
        //if(true) return;
        Result result=junit.run(request);
        System.out.println(result);
        System.out.println(result.wasSuccessful());
        Class<ConcreteGoServerTestCase> clazz2=ConcreteGoServerTestCase.class;
        String method2="testPlayOneMove";
        Request request2=Request.method(clazz2,method2);
        Result result2=junit.run(request2);
        System.out.println(result2.wasSuccessful());
        NamedThreadGroup.stopAllStopables();
        System.out.println(NamedThreadGroup.printNamedThreadGroups(true));
        Thread.sleep(100);
        System.out.println("|||");
        System.out.println(NamedThreadGroup.printNamedThreadGroups(true));
        System.out.println("exit");
        NamedThreadGroup.printThraedsAtEnd();
    }
    
}
