package sgf;
import static sgf.Parser.sgfData;
import java.util.Collection;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.MyTestWatcher;
@RunWith(Parameterized.class) public class ParserTestCase extends AbstractParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> parameters() { return sgfData(); }
    public ParserTestCase(String key) { this.key=key; }
}
