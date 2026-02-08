package io;
import utilities.MyTestWatcher;
import static controller.GTPBackEnd.timeoutTime;
import org.junit.*;
import controller.GTPBackEnd;
public class TimeoutTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    @After public void tearDown() throws Exception {}
    @Ignore @Test(timeout=timeoutTime) public void testTimeout() throws Exception {
        boolean forever=true;
        while(true) if(forever) GTPBackEnd.sleep2(GTPBackEnd.yield);
        else break;
    }
    @Ignore @Test(timeout=timeoutTime) public void timeoutTestCaseTest1() {
        boolean forever=true;
        while(true) if(!forever) break;
    }
}

