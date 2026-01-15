package sgf;
import org.junit.*;
import utilities.MyTestWatcher;
import utilities.TestKeys;
public class MultipleGamesTestCase extends AbstractMultipleGamesTestCase {
    @Override @Before public void setUp() throws Exception {
        key=TestKeys.sgfExampleFromRedBean;
    }
}
