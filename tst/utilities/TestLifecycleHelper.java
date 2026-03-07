package utilities;

import java.util.ArrayList;

import controller.GTPBackEnd;
import io.IOs;
import io.Logging;
import server.NamedThreadGroup;

/**
 * Test-only lifecycle state and teardown logic that used to live in Init.
 */
public final class TestLifecycleHelper {
    private TestLifecycleHelper() {}

    public static void initiaizeTests() {
        Logging.mainLogger.info("suit contols: " + suiteControls);
        Logging.mainLogger.info("wrapup counter: " + counter);
        if(suiteControls) return;
        ++counter;
        maxCounter = Math.max(counter, maxCounter);
    }

    public static void wrapupTests_() {
        wasWrapupTestsCalled = true;
        Logging.mainLogger.info("wrapup tests");
        Logging.mainLogger.info("highest named threads id: " + NamedThreadGroup.ids);
        Logging.mainLogger.info(NamedThreadGroup.allNamedThreads.size() + "/" + NamedThreadGroup.ids);
        NamedThreadGroup.printNamedThreads(NamedThreadGroup.allNamedThreads, "all", false);
        IOs.printThreads(IOs.activeThreads(), "active at end", false);
        GTPBackEnd.sleep2(2); // was 10
        NamedThreadGroup.removeAllTerminated();
        Logging.mainLogger.info("highest named threads id: " + NamedThreadGroup.ids);
        Logging.mainLogger.info(NamedThreadGroup.allNamedThreads.size() + "/" + NamedThreadGroup.ids);
        NamedThreadGroup.printNamedThreads(NamedThreadGroup.allNamedThreads, "all", false);
        IOs.printThreads(IOs.activeThreads(), "active at end", false);
    }

    public static synchronized void wrapupTests() {
        boolean printMore = false;
        if(printMore) {
            IOs.stackTrace(3);
            Logging.mainLogger.info("wrapup counter: " + counter + ", max counter: " + maxCounter);
        }
        if(wasWrapupTestsCalled) Logging.mainLogger.severe("duplicate call to wrapup");
        if(suiteControls) return;
        if(--counter > 0) {
            Logging.mainLogger.info("counter: " + counter + " not wrapping up");
            return;
        }
        Logging.mainLogger.info("not returning");
        Logging.mainLogger.info("counter: " + counter);
        wrapupTests_();
    }

    public static void reset() {
        counter = 0;
        maxCounter = 0;
        wasWrapupTestsCalled = false;
        suiteControls = false;
        testsRun.clear();
    }

    public static int counter;
    public static boolean suiteControls;
    public static final ArrayList<String> testsRun = new ArrayList<>();
    static boolean wasWrapupTestsCalled;
    static int maxCounter;
}
