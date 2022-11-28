package sgf;
import org.junit.*;
import utilities.MyTestWatcher;
public class SgfRoundTripTestCase extends AbstractSgfRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception {
        key=Parser.empty;
        key="sgfExamleFromRedBean";
        
        super.setUp();
    }
}
