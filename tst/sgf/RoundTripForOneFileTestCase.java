package sgf;
import org.junit.*;
import utilities.MyTestWatcher;
public class RoundTripForOneFileTestCase extends AbstractSgfParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception {
        key="smartgo4";
        key="comments1";
        key="oneMoveAtA1";
        key="smartgovariationsflat";
        key="newvariationssmall";
        key="twoGamesInOneFileFromSmartGo";
        key="twoVariationsAtDifferentMoves";
        key="twoverysmallgamesflat";
        key="twoEmptyWithSemicolon";
        key="emptyWithSemicolon";
        key="reallyEmpty";
        super.setUp();
    }
}
