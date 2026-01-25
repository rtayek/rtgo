package server;
import io.Logging;
import org.junit.*;
import org.junit.runner.*;
import io.IOs;
// tries to runb a test programmatically from main();
public class ConcreteGoServerTestCase extends AbstractGoServerTestCase {
    @Override @Before public void setUp() throws Exception {
        serverPort=IOs.anyPort;
        super.setUp();
    }
    @Override @After public void tearDown() throws Exception { Logging.mainLogger.info("teardown"); super.tearDown(); }
    public static void main(String[] args) throws Exception {
        // run a few test methods
        JUnitCore junit=new JUnitCore();
        Class<ConcreteGoServerTestCase> clazz=ConcreteGoServerTestCase.class;
        String method="test";
        Logging.mainLogger.info("get request");
        Request request=Request.method(clazz,method);
        Logging.mainLogger.info(String.valueOf(request));
        String method3="adasdsd";
        Logging.mainLogger.info("get request");
        Request request3=Request.method(clazz,method3);
        Logging.mainLogger.info(String.valueOf(request3));
        Result result=junit.run(request);
        Logging.mainLogger.info("result: "+result);
        Logging.mainLogger.info("was successful: "+result.wasSuccessful());
        Class<ConcreteGoServerTestCase> clazz2=ConcreteGoServerTestCase.class;
        String method2="testPlayOneMove";
        Request request2=Request.method(clazz2,method2);
        Result result2=junit.run(request2);
        Logging.mainLogger.info(method2+" "+result2.wasSuccessful());
        NamedThreadGroup.stopAllStopables();
        Logging.mainLogger.info(String.valueOf(NamedThreadGroup.printNamedThreadGroups(true)));
        Thread.sleep(10);
        Logging.mainLogger.info("|||");
        Logging.mainLogger.info(String.valueOf(NamedThreadGroup.printNamedThreadGroups(true)));
        Logging.mainLogger.info("exit");
        NamedThreadGroup.printThraedsAtEnd();
    }
}
