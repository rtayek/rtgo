package controller;
import static org.junit.Assert.*;
import java.io.IOException;
import org.junit.*;
import io.*;
import io.IO.Stopable;
import model.Model;
import server.NamedThreadGroup;
import server.NamedThreadGroup.NamedThread;
import utilities.MyTestWatcher;
public /*abstract*/ class TeardownOrderTestCase {
    // this is confused. looks like a runner for both.
    // but it tries to stop recorder, black, and white which are null!
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    //static class
    class Runner implements Runnable,Stopable {
        Runner(BothEnds both) { this.both=both; }
        public @Override void run() {
            while(true) { // no way to break out
                Response response=both.frontEnd.sendAndReceive(Command.name.name()+'\n');
                assertNotNull(response);
                assertTrue(response.isOk());
                assertEquals(Model.sgfApplicationName,response.response);
            }
        }
        @Override public boolean isStopping() { return isStopping; }
        @Override public boolean setIsStopping() { boolean rc=isStopping; isStopping=true; return rc; }
        @Override public void stop() {
            isStopping=true;
            IO.myClose(null,null,null,(NamedThread)thread,both.frontEnd.name,this);
        }
        final BothEnds both;
        Thread thread;
        transient boolean isStopping;
    }
    void start() {
        NamedThread back=recorder.backEnd.startGTP(0);
        if(back==null) {
            Logging.mainLogger.severe("test 11 startGTP returns null!");
            if(GTPBackEnd.throwOnstartGTPFailure) throw new RuntimeException("test 11 can not run backend!");
        }
        Runner recorderRunner=new Runner(recorder);
        (recorderRunner.thread=NamedThreadGroup.createNamedThread(NamedThreadGroup.groupZero,recorderRunner,"recorder"))
                .start();
        back=black.backEnd.startGTP(0);
        if(back==null) {
            Logging.mainLogger.severe("test 12 startGTP returns null!");
            if(GTPBackEnd.throwOnstartGTPFailure) throw new RuntimeException("12 can not run backend!");
        }
        Runner blackRunner=new Runner(black);
        (blackRunner.thread=NamedThreadGroup.createNamedThread(0,blackRunner,"black")).start();
        back=white.backEnd.startGTP(0);
        if(back==null) {
            Logging.mainLogger.severe("test 13 startGTP returns null!");
            if(GTPBackEnd.throwOnstartGTPFailure) throw new RuntimeException("13 can not run backend!");
        }
        Runner whiteRunner=new Runner(white);
        (whiteRunner.thread=NamedThreadGroup.createNamedThread(0,whiteRunner,"white")).start();
    }
    @Ignore @Test public void testTearDownFrontEndFirst() throws IOException,InterruptedException {
        start();
        Thread.sleep(100);
        recorder.stop();
        // this is bogus, same order!
        black.stop();
        white.stop();
    }
    // try other teardown orders?
    @Ignore @Test public void testTearDownBackEndFirst() throws Exception {
        start();
        Thread.sleep(10);
        recorder.stop();
        black.stop();
        white.stop();
    }
    BothEnds recorder,black,white;
}
