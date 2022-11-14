package sgf;
import static sgf.Parser.*;
import java.util.*;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.*;
@RunWith(Parameterized.class) public class ParameterizedSgfRoundTripTestCase extends AbstractSgfRoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> parameters() {
        Set<Object> objects=new LinkedHashSet<>();
        objects.addAll(sgfDataKeySet());
        objects.addAll(sgfFiles());
        return ParameterArray.parameterize(objects);
    }
    public ParameterizedSgfRoundTripTestCase(Object key) { this.key=key; }
}
