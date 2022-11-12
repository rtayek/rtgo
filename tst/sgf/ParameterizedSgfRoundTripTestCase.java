package sgf;
import java.util.Collection;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.*;
@RunWith(Parameterized.class) public class ParameterizedSgfRoundTripTestCase extends AbstractSgfRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> parameters() {
        return ParameterArray.parameterize(Parser.sgfTestData());
    }
    public ParameterizedSgfRoundTripTestCase(Object key) { this.key=key; }
}
