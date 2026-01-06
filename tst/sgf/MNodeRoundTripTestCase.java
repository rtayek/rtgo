package sgf;
import org.junit.*;
import utilities.MyTestWatcher;
import utilities.TestKeys;
public class MNodeRoundTripTestCase extends AbstractMNodeRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception {
        key=TestKeys.sgfExampleFromRedBean;
    }
}
