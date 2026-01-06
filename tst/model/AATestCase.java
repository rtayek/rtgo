package model;
import org.junit.Before;
import org.junit.Rule;
import sgf.AbstractMNodeRoundTripTestCase;
import utilities.MyTestWatcher;
import utilities.TestKeys;
// why does this pass?
public class AATestCase extends AbstractMNodeRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception {
        key=TestKeys.oneMoveAtA1;
    }
}
