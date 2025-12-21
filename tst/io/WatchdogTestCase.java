package io;
import static org.junit.Assert.*;
import java.lang.Thread.State;
import java.util.Set;
import java.util.logging.Logger;
import org.junit.*;
import controller.GTPBackEnd;
import server.NamedThreadGroup;
import utilities.*;
@Ignore public class WatchdogTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {
        String name="main";
        Thread target=NamedThreadGroup.findThread(name);
        if(target!=null) {
            watchdog=new Watchdog(target,watchdogTestTimeoutTime);
            watchdog.start();
        } else {
            Logging.mainLogger.severe("watchdog can not find name: "+name);
            fail("watchdog can not find name: "+name);
        }
    }
    @After public void tearDown() throws Exception {
        Set<Thread> threads=IOs.activeThreads();
        System.out.println(threads);
    }
    @Test(timeout=watchdogTimeoutTime) public void testInfiniteLoop() {
        // watchdog goes off and
        // Thread[watchdog,5,main] w joined with: Thread[main,5,main]
        // watch run method exits
        System.out.println(Thread.currentThread());
        if(hack) while(true) GTPBackEnd.sleep2(GTPBackEnd.yield);
        assertTrue(false);
    }
    @Test(timeout=watchdogTimeoutTime) public void testDone() {
        GTPBackEnd.sleep2(watchdogTestTimeoutTime/4);
        watchdog.done=true;
        GTPBackEnd.sleep2(10);
        assertEquals("watchdog is not alive",false,watchdog.isAlive());
        assertEquals("watchdog is terminated.",State.TERMINATED,watchdog.getState());
    }
    // maybe give a lambda to the watchdog that will call fail?
    @Test(timeout=watchdogTimeoutTime) public void testThatThisTimesOutWithATimeout() {
        try {
            while(!Thread.currentThread().isInterrupted()) Thread.yield();
            System.out.println("we got interrupted or something at "+et);
            assertTrue(true);
        } catch(Exception e) {
            System.out.println("caught:"+e);
            fail("caught:"+e);
        }
    }
    @Test(timeout=watchdogTimeoutTime) public void testThatThisTimesOutWithoutATimeout() {
        // looks like the same as above., fix!
        // maybe start watchdog in each test?
        try {
            while(!Thread.currentThread().isInterrupted()) Thread.yield();
            System.out.println("we got interrupted or something at "+et);
            assertTrue(true);
        } catch(Exception e) {
            System.out.println("caught:"+e);
            fail("caught:"+e);
        }
    }
    int watchdogTestTimeoutTime=500;
    final int watchdogTimeoutTime=1000;
    Watchdog watchdog;
    boolean hack=true;
    Logger logger=Logging.mainLogger;
    Et et=new Et();
    static {
        System.out.println("static init: "+Init.first.et);
    }
}
