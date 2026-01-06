package sgf;
import org.junit.Rule;
import utilities.MyTestWatcher;
import utilities.TestKeys;
public class SgfRoundTripTestCase extends AbstractSgfRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    public SgfRoundTripTestCase() {
        key=TestKeys.emptyWithSemicolon;
    }
}
