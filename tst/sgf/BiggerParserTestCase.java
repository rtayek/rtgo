package sgf;
import static sgf.Parser.sgfTestData;
import java.util.Collection;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.*;
@RunWith(Parameterized.class) public class BiggerParserTestCase extends AbstractParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> parameters() {
        return ParameterArray.parameterize(sgfTestData());
    }
    public BiggerParserTestCase(Object key) { this.key=key;
    System.out.println("key: "+key);
    }
}
