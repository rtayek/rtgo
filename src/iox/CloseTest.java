package iox;
import java.io.*;
import java.net.*;
import java.util.Set;
import io.IO;
public class CloseTest {
    Thread server() {
        Thread server=new Thread(new Runnable() {
            @Override public void run() {
                System.out.println("server thread enter run().");
                try {
                    Thread.sleep(100);
                    InetAddress host=InetAddress.getLocalHost();
                    Socket s=new Socket(host,PORT);
                    System.out.println("server wait");
                    synchronized(this) { // why not just sleep?
                        while(!done) wait(10);
                        // investigate wait vs sleep.
                    }
                    System.out.println("end of server wait");
                } catch(Exception e) {
                    System.out.println("server caught: "+e);
                    System.out.println("server caught: "+e.getMessage());
                }
                System.out.println("server thread exit run().");
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
                    System.out.println("reader is reading ...");
                    System.out.println("1 reader: "+IO.toString(reader));
                    String s=bufferedReader.readLine();
                    // should never get here
                    System.out.println("reader returned "+s);
                } catch(Exception e) {
                    // This is the behavior we want, but never get here
                    System.out.println("reader aborted with "+e.getMessage());
                }
                done=true;
            }
        };
        Runnable runnable=readerRunnable;
        return new Thread(runnable,"reader");
    }
    Thread closer(final Reader reader) {
        Runnable r=()->System.out.println();
        Runnable runnable=new Runnable() {
            @Override public void run() {
                try {
                    System.out.println("closer started ... ");
                    Thread.sleep(100); // give reader time to start
                    System.out.println("closing reader ...");
                    System.out.println("closer: "+closer);
                    reader.close();
                    System.out.println("closing reader complete");
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
        System.out.println("enter main run()");
        testBufferedReader();
        Thread.sleep(400);
        boolean printActiveThreads=false;
        Set<Thread> activeThreads=IO.activeThreads();
        if(printActiveThreads) {
            System.out.println("active threads");
            for(Thread thread:activeThreads) { System.out.println("ct run "+IO.toString(thread)); }
        }
        System.out.println("interrupt server, reader and closer.");
        //server.interrupt();
        reader.interrupt();
        closer.interrupt();
        Thread.sleep(500);
        //testDataInputStream();
        activeThreads=IO.activeThreads();
        if(printActiveThreads) {
            System.out.println("active threads");
            for(Thread thread:activeThreads) { System.out.println("ct run 2 "+IO.toString(thread)); }
        }
        System.out.println("exit main run().");
    }
    public static void main(String[] args) throws Exception {
        new CloseTest().run();
        System.out.println("exit main().");
    }
    ServerSocket serverSocket;
    Thread server;
    Thread reader;
    Thread closer;
    //Thread socket;
    boolean done;
    static int PORT=9099;
}
