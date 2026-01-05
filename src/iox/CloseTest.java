package iox;
import io.Logging;
import java.io.*;
import java.net.*;
import java.util.Set;
import io.IOs;
public class CloseTest {
    Thread server() {
        Thread server=new Thread(new Runnable() {
            @Override public void run() {
                Logging.mainLogger.info("server thread enter run().");
                Socket socket=null;
                try {
                    Thread.sleep(100);
                    InetAddress host=InetAddress.getLocalHost();
                    socket=new Socket(host,PORT);
                    Logging.mainLogger.info("server wait");
                    synchronized(this) { // why not just sleep?
                        while(!done) wait(10);
                        // investigate wait vs sleep.
                    }
                    Logging.mainLogger.info("end of server wait");
                } catch(Exception e) {
                    Logging.mainLogger.info("server caught: "+e);
                    Logging.mainLogger.info("server caught: "+e.getMessage());
                } finally {
                    if(socket!=null) try {
                        socket.close();
                    } catch(IOException ignored) {}
                }
                Logging.mainLogger.info("server thread exit run().");
            }
        },"server");
        return server;
    }
    BufferedReader connect() throws IOException {
        Socket socket=serverSocket.accept();
        InputStream inputStream=socket.getInputStream();
        InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
        BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
        return bufferedReader;
    }
    Thread reader(BufferedReader bufferedReader) {
        Runnable readerRunnable=new Runnable() {
            @Override public void run() {
                try {
                    Logging.mainLogger.info("reader is reading ...");
                    Logging.mainLogger.info("1 reader: "+IOs.toString(reader));
                    String s=bufferedReader.readLine();
                    // should never get here
                    Logging.mainLogger.info("reader returned "+s);
                } catch(Exception e) {
                    // This is the behavior we want, but never get here
                    Logging.mainLogger.info("reader aborted with "+e.getMessage());
                }
                done=true;
            }
        };
        Runnable runnable=readerRunnable;
        return new Thread(runnable,"reader");
    }
    Thread closer(final Reader reader) {
        Runnable r=()->Logging.mainLogger.info("");
        Runnable runnable=new Runnable() {
            @Override public void run() {
                try {
                    Logging.mainLogger.info("closer started ... ");
                    Thread.sleep(100); // give reader time to start
                    Logging.mainLogger.info("closing reader ...");
                    Logging.mainLogger.info("closer: "+closer);
                    reader.close();
                    Logging.mainLogger.info("closing reader complete");
                } catch(Exception e) {}
            }
        };
        return new Thread(runnable,"closer");
    }
    void testBufferedReader() throws Exception {
        serverSocket=new ServerSocket(PORT);
        server=server();
        server.start();
        Thread.sleep(100);
        BufferedReader bufferedReader=connect();
        reader=reader(bufferedReader);
        closer=closer(bufferedReader);
        reader.start();
        Thread.sleep(100);
        closer.start();
        Thread.sleep(100);
    }
    void run() throws Exception {
        Logging.mainLogger.info("enter main run()");
        testBufferedReader();
        Thread.sleep(400);
        boolean printActiveThreads=false;
        Set<Thread> activeThreads=IOs.activeThreads();
        if(printActiveThreads) {
            Logging.mainLogger.info("active threads");
            for(Thread thread:activeThreads) { Logging.mainLogger.info("ct run "+IOs.toString(thread)); }
        }
        Logging.mainLogger.info("interrupt server, reader and closer.");
        //server.interrupt();
        reader.interrupt();
        closer.interrupt();
        Thread.sleep(500);
        //testDataInputStream();
        activeThreads=IOs.activeThreads();
        if(printActiveThreads) {
            Logging.mainLogger.info("active threads");
            for(Thread thread:activeThreads) { Logging.mainLogger.info("ct run 2 "+IOs.toString(thread)); }
        }
        Logging.mainLogger.info("exit main run().");
    }
    public static void main(String[] args) throws Exception {
        new CloseTest().run();
        Logging.mainLogger.info("exit main().");
    }
    ServerSocket serverSocket;
    Thread server;
    Thread reader;
    Thread closer;
    //Thread socket;
    boolean done;
    static int PORT=9099;
}
