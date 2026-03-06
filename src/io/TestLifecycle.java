package io;

import controller.GTPBackEnd;
import server.NamedThreadGroup;

/**
 * Test lifecycle operations extracted from Init.
 * Behavior is intentionally unchanged; Init currently delegates here.
 */
public class TestLifecycle {
    public void initiaizeTests(Init init) {
        Logging.mainLogger.info("suit contols: "+init.suiteControls);
        Logging.mainLogger.info("wrapup counter: "+init.counter);
        if(init.suiteControls) return;
        // should i get initial ids here?
        ++init.counter;
        Init.maxCounter=Math.max(init.counter,Init.maxCounter);
    }

    public void wrapupTests_(Init init) {
        init.wasWrapupTestsCalled=true;
        Logging.mainLogger.info("wrapup tests");
        Logging.mainLogger.info("highest named threads id: "+NamedThreadGroup.ids);
        Logging.mainLogger.info(NamedThreadGroup.allNamedThreads.size()+"/"+NamedThreadGroup.ids);
        NamedThreadGroup.printNamedThreads(NamedThreadGroup.allNamedThreads,"all",false);
        IOs.printThreads(IOs.activeThreads(),"active at end",false);
        GTPBackEnd.sleep2(2); // was 10
        NamedThreadGroup.removeAllTerminated();
        Logging.mainLogger.info("highest named threads id: "+NamedThreadGroup.ids);
        Logging.mainLogger.info(NamedThreadGroup.allNamedThreads.size()+"/"+NamedThreadGroup.ids);
        NamedThreadGroup.printNamedThreads(NamedThreadGroup.allNamedThreads,"all",false);
        IOs.printThreads(IOs.activeThreads(),"active at end",false);
    }

    public synchronized void wrapupTests(Init init) {
        boolean printMore=false;
        if(printMore) {
            IOs.stackTrace(3);
            Logging.mainLogger.info("wrapup counter: "+init.counter+", max counter: "+Init.maxCounter);
        }
        if(init.wasWrapupTestsCalled) Logging.mainLogger.severe("duplicate call to wrapup");
        if(init.suiteControls) return;
        else {
            if(--init.counter>0) {
                Logging.mainLogger.info("counter: "+init.counter+" not wrapping up");
                return;
            }
            Logging.mainLogger.info("not returning");
        }
        Logging.mainLogger.info("counter: "+init.counter);
        wrapupTests_(init);
    }

    public void lastPrint(Init init) {
        NamedThreadGroup.printThraedsAtEnd();
        int n=NamedThreadGroup.printNamedThreadGroups(true);
        Logging.mainLogger.info(String.valueOf(n));
        IOs.printThreads(IOs.activeThreads(),"last",true);
        Logging.mainLogger.info("tests run: "+init.testsRun);
    }
}
