package io;
import static org.junit.Assert.assertEquals;
import java.io.*;
import org.junit.*;
import utilities.TestSupport;
class Looper implements Runnable {
    @Override public void run() {
        try {
            while(!done) { ; }
        } catch(Exception e) {
            Logging.mainLogger.info(Thread.currentThread()+" caught: "+e);
        }
    }
    boolean done;
}
class Yielder implements Runnable {
    @Override public void run() {
        try {
            while(!done) {
                Thread.yield();
                if(Thread.interrupted()) done=true;
            }
        } catch(Exception e) {
            Logging.mainLogger.info(Thread.currentThread()+" caught: "+e);
        }
    }
    boolean done;
}
class Sleeper implements Runnable {
    Sleeper(int n) { this.n=n; }
    @Override public void run() {
        try {
            while(!done) {
                Thread.sleep(n);
                if(Thread.interrupted()) done=true;
            }
        } catch(Exception e) {
            Logging.mainLogger.info(Thread.currentThread()+" caught: "+e);
        }
    }
    final int n;
    boolean done;
}
class MyReader implements Runnable { // maybe call this cat?
    MyReader() throws IOException {
        pout=new PipedOutputStream();
        pin=new PipedInputStream(pout);
        in=new BufferedReader(new InputStreamReader(pin));
        this.out=new OutputStreamWriter(pout);
    }
    @Override public void run() {
        try { // can't possibly work - needs to write before reading!
            while(!done) {
                String string=in.readLine(); // xyzzy
                if(string==null) { done=true; break; }
                out.write(string+'\n');
            }
            out.flush();
            //out.close();
        } catch(Exception e) {
            Logging.mainLogger.info(Thread.currentThread()+"caught: "+e);
        } finally {
            try {
                in.close();
            } catch(IOException ignored) {
            }
            try {
                out.close();
            } catch(IOException ignored) {
            }
            try {
                pin.close();
            } catch(IOException ignored) {
            }
            try {
                pout.close();
            } catch(IOException ignored) {
            }
        }
    }
    final BufferedReader in;
    final Writer out;
    final PipedInputStream pin;
    final PipedOutputStream pout;
    boolean done;
}
public class ThreadTestCase extends TestSupport {
    @Test public void testYielder() throws InterruptedException {
        int old=Thread.activeCount();
        Thread thread=new Thread(new Yielder(),"yielder");
        thread.start();
        Thread.sleep(0);
        thread.interrupt();
        thread.join();
        // maybe use named threads to see exactly what the problem is.
        assertEquals(old,Thread.activeCount());
    }
    @Test public void testLooper() throws InterruptedException {
        int old=Thread.activeCount();
        Thread thread=new Thread(new Yielder(),"looper");
        thread.start();
        Thread.sleep(0);
        thread.interrupt();
        thread.join();
        assertEquals(old,Thread.activeCount());
    }
    @Test() public void testSleeper0() throws InterruptedException {
        int old=Thread.activeCount();
        Thread thread=new Thread(new Sleeper(0),"sleeper 0");
        thread.start();
        Thread.sleep(0);
        thread.interrupt();
        thread.join();
        assertEquals(old,Thread.activeCount());
    }
    @Test public void testSleeper10() throws InterruptedException {
        int old=Thread.activeCount();
        Thread thread=new Thread(new Sleeper(10),"sleeper 10");
        thread.start();
        Thread.sleep(0);
        thread.interrupt();
        thread.join();
        assertEquals(old,Thread.activeCount());
    }
    @Test public void testReader() throws Exception {
        int old=Thread.activeCount();
        Thread thread=new Thread(new MyReader(),"reader");
        thread.start();
        Thread.sleep(0);
        thread.interrupt();
        thread.join();
        // 1/22/23 failed  once
        assertEquals(old,Thread.activeCount());
    }
    public static void print(Thread thread) {
        Logging.mainLogger.info("is alive: "+thread.isAlive());
        Logging.mainLogger.info("is interrupted: "+thread.isInterrupted());
    }
}
