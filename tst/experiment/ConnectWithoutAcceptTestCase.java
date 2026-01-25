package experiment;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.net.*;
import org.junit.*;
import utilities.TestSupport;
public class ConnectWithoutAcceptTestCase extends TestSupport {
    private void check() throws IOException {
        ServerSocket serverSocket=null;
        try {
            serverSocket=new ServerSocket(port);
            Socket socket=new Socket();
            InetSocketAddress inetSocketAddress=new InetSocketAddress("localhost",port);
            socket.connect(inetSocketAddress,100);
            assertTrue(socket.isConnected());
            socket.close();
        } finally {
            if(serverSocket!=null) serverSocket.close();
        }
    }
    @Test public void testOne() throws Exception { check(); }
    @Test public void testMore() throws Exception {
        // this does not fail like
        for(int i=0;i<10;i++) check();
    }
    static int port=12345;
}
