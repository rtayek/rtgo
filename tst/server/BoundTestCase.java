package server;
import io.Logging;
import static org.junit.Assert.*;
import org.junit.*;
import io.IOs;
import utilities.TestSupport;
public class BoundTestCase extends TestSupport {
    @Test public void testLogicForAlreadyBoundSocket() throws Exception {
        GoServer goServer=GoServer.startServer(IOs.testPort);
        assertTrue(goServer.serverSocket.isBound());
        int x=goServer.serverSocket.getLocalPort();
        Logging.mainLogger.info(String.valueOf(x));
        GoServer goServer2=GoServer.startServer(IOs.testPort);
        assertTrue(goServer2.serverSocket.isBound());
        assertNotEquals(goServer.serverSocket.getLocalPort(),goServer2.serverSocket.getLocalPort());
        goServer.stop();
        goServer2.stop();
    }
}
