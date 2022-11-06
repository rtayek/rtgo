package server;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import org.junit.*;
import io.*;
import io.IO.Stopable;
import server.NamedThreadGroup.NamedThread;
import utilities.MyTestWatcher;
public class NamedThreadsTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @BeforeClass public static void setUpBeforeClass() throws Exception {}
    @AfterClass public static void tearDownAfterClass() throws Exception {}
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    static class R implements Runnable,Stopable {
        @Override public void run() {
            try {
                Thread.sleep(100);
            } catch(InterruptedException e) {
                Logging.mainLogger.warning(this+" caught: "+e);
            }
        }
        @Override public boolean isStopping() { return isStopping; }
        @Override public boolean setIsStopping() { boolean rc=isStopping; isStopping=true; return rc; }
        @Override public void stop() throws IOException,InterruptedException {}
        boolean isStopping;
    };
    static class R2 implements Runnable,Stopable {
        @Override public void run() {
            try {
                Thread.sleep(100);
            } catch(InterruptedException e) {
                Logging.mainLogger.warning(this+" caught: "+e);
            }
        }
        @Override public boolean isStopping() { return isStopping; }
        @Override public boolean setIsStopping() { boolean rc=isStopping; isStopping=true; return rc; }
        @Override public void stop() throws IOException,InterruptedException {}
        boolean isStopping;
    };
    static class R3 implements Runnable {
        @Override public void run() {
            while(!done) try {
                Thread.sleep(10);
            } catch(InterruptedException e) {
                Logging.mainLogger.warning(this+" caught: "+e);
            }
        }
        boolean done;
    };
    @Test() public void testAddNamedThread() throws Exception {
        NamedThreadGroup namedThreadGroup=new NamedThreadGroup(1);
        R r1=new R();
        //OuterClass.InnerClass innerObject = outerObject.new InnerClass();
        NamedThread thread=namedThreadGroup.new NamedThread("0",r1);
        namedThreadGroup.addNamedThread(thread);
        assertTrue(namedThreadGroup.namedThreadsWithRunnables.contains(thread));
        IO.myClose(null,null,null,thread,null,r1);
    }
    @Test() public void testRs() throws Exception {
        R r1=new R();
        NamedThread thread1=namedThreadGroup.new NamedThread("R1-1",r1);
        thread1.start();
        R2 r2=new R2();
        NamedThread thread2=namedThreadGroup.new NamedThread("R2-1",r2);
        thread2.start();
        //print3();
        IO.myClose(null,null,null,thread1,"thread 1",r1);
        //Thread.sleep(10); //
        //print3();
        //System.out.println(namedThreadGroup.activeNamedThreads().size());
        assertTrue(namedThreadGroup.activeNamedThreads().size()==0);
    }
    @Test() public void testUnamedThread() throws InterruptedException {
        R3 r3=new R3();
        Thread thread=new Thread();
        thread.start();
    }
    @Test() public void testNamedThread() throws InterruptedException {
        R3 r3=new R3();
        NamedThread thread=namedThreadGroup.new NamedThread("R3-1",r3);
        thread.start();
    }
    @Test() public void testNamedThreads() throws InterruptedException {
        R3 r3=new R3();
        // use groupZero!
        Thread thread=NamedThreadGroup.createNamedThread(0,r3,"R3-2");
        thread.start();
    }
    @Test() public void testStartLikeStartGTP() throws InterruptedException {
        R3 r3=new R3();
        Thread thread;
        (thread=NamedThreadGroup.createNamedThread(0,r3,"R3-3")).start();
    }
    private NamedThreadGroup namedThreadGroup=new NamedThreadGroup(NamedThreadGroup.standAlone);
}
