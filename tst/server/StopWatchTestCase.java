package server;
import utilities.MyTestWatcher;
import static org.junit.Assert.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.junit.*;
import org.junit.rules.Stopwatch;
import org.junit.runner.Description;
public class StopWatchTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    private static final Logger logger=Logger.getLogger("");
    private static void logInfo(Description description,String status,long nanos) {
        String testName=description.getMethodName();
        logger.info(String.format("Test %s %s, spent %d microseconds",testName,status,
                TimeUnit.NANOSECONDS.toMicros(nanos)));
    }
    @Rule public Stopwatch stopwatch=new Stopwatch() {
        @Override protected void succeeded(long nanos,Description description) {
            logInfo(description,"succeeded",nanos);
        }
        @Override protected void failed(long nanos,Throwable e,Description description) {
            logInfo(description,"failed",nanos);
        }
        @Override protected void skipped(long nanos,AssumptionViolatedException e,Description description) {
            logInfo(description,"skipped",nanos);
        }
        @Override protected void finished(long nanos,Description description) { logInfo(description,"finished",nanos); }
    };
    @Test public void succeeds() {}
    @Ignore @Test public void fails() { fail(); }
    @Ignore @Test public void performanceTest() throws InterruptedException {
        // An example to assert runtime:
        long delta=20;
        Thread.sleep(300L);
        assertEquals(300d,stopwatch.runtime(TimeUnit.MILLISECONDS),delta);
        Thread.sleep(500L);
        assertEquals(800d,stopwatch.runtime(TimeUnit.MILLISECONDS),delta);
        // this seems fragile.
    }
}

