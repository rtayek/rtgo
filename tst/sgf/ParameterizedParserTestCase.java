package sgf;
import java.util.Collection;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.*;
@RunWith(Parameterized.class) public class ParameterizedParserTestCase extends AbstractParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> parameters() {
        //return ParameterArray.parameterize(Parser.sgfDataKeySet());
        return ParameterArray.parameterize(Parser.sgfTestData());
    }
    public ParameterizedParserTestCase(Object key) { this.key=key; }
}
