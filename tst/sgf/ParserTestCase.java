package sgf;
import org.junit.Rule;
import utilities.MyTestWatcher;
import utilities.TestKeys;
public class ParserTestCase extends AbstractSgfParserTestCase {
    public ParserTestCase() {
        key=TestKeys.sgfExampleFromRedBean;
    }
}
