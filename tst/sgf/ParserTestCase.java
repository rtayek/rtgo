package sgf;
import org.junit.Rule;
import utilities.MyTestWatcher;
import utilities.TestKeys;
public class ParserTestCase extends AbstractSgfParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    public ParserTestCase() {
        key=TestKeys.sgfExampleFromRedBean;
    }
}
