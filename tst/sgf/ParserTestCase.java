package sgf;
import org.junit.*;
import utilities.MyTestWatcher;
public class ParserTestCase extends AbstractParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception { key=Parser.empty; super.setUp(); }
}
