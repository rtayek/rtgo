package x;
import io.Logging;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.*;
public class SocketChannelServer {
    // seems like this is the client
    // as it is writing to the server
    public static void main(String[] args) throws IOException {
        SocketChannel server=SocketChannel.open();
        SocketAddress socketAddr=new InetSocketAddress("localhost",9000);
        server.connect(socketAddr);
        Path path=Paths.get("C:/Test/temp.txt");
        FileChannel fileChannel=FileChannel.open(path);
        ByteBuffer buffer=ByteBuffer.allocate(1024);
        while(fileChannel.read(buffer)>0) {
            buffer.flip();
            server.write(buffer);
            buffer.clear();
        }
        fileChannel.close();
        Logging.mainLogger.info("File Sent");
        server.close();
    }
    static final String filename="temp.txt";
}
