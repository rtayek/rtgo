package sgf;
import org.junit.Rule;
import utilities.MyTestWatcher;
import utilities.TestKeys;
public class SgfRoundTripTestCase extends AbstractSgfRoundTripTestCase {
    public SgfRoundTripTestCase() {
        key=TestKeys.emptyWithSemicolon;
    }
}
