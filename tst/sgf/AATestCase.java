package sgf;
import utilities.TestKeys;
// why does this pass?
public class AATestCase extends AbstractMNodeRoundTripTestCase {
    @Override protected Object defaultKey() {
        return TestKeys.oneMoveAtA1;
    }
}
