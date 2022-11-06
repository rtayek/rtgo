package server;
import static org.junit.Assert.*;
import org.junit.*;
import io.IO;
import utilities.MyTestWatcher;
public class BoundTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testLogicForAlreadyBoundSocket() throws Exception{
        GoServer goServer=GoServer.startServer(IO.testPort);
        assertTrue(goServer.serverSocket.isBound());
        int x=goServer.serverSocket.getLocalPort();
        System.out.println(x);
        GoServer goServer2=GoServer.startServer(IO.testPort);
        assertTrue(goServer2.serverSocket.isBound());
        assertNotEquals(goServer.serverSocket.getLocalPort(),goServer2.serverSocket.getLocalPort());
        goServer.stop();
        goServer2.stop();
    }
}
