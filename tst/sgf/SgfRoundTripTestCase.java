package sgf;
import utilities.TestKeys;
public class SgfRoundTripTestCase extends AbstractSgfRoundTripTestCase {
    @Override protected Object defaultKey() {
        return TestKeys.emptyWithSemicolon;
    }
}
