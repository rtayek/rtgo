package x;
import io.Logging;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.EnumSet;
public class SocketChannelClient {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocket=null;
        SocketChannel client=null;
        serverSocket=ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(9000));
        client=serverSocket.accept();
        Logging.mainLogger.info("Connection Set:  "+client.getRemoteAddress());
        Path path=Paths.get(filename);
        FileChannel fileChannel=FileChannel.open(path,
                EnumSet.of(StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.WRITE));
        ByteBuffer buffer=ByteBuffer.allocate(1024);
        while(client.read(buffer)>0) {
            buffer.flip();
            fileChannel.write(buffer);
            buffer.clear();
        }
        fileChannel.close();
        Logging.mainLogger.info("File Received");
        client.close();
    }
    static final String filename="temp1.txt";
}