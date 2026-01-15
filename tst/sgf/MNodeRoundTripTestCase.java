package sgf;
import org.junit.*;
import utilities.MyTestWatcher;
import utilities.TestKeys;
public class MNodeRoundTripTestCase extends AbstractMNodeRoundTripTestCase {
    @Override @Before public void setUp() throws Exception {
        key=TestKeys.sgfExampleFromRedBean;
    }
}
