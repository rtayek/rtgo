package model;
import static sgf.Parser.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import sgf.Parser;
import utilities.*;
@RunWith(Parameterized.class) public class ParameterizedModelRoundTripTestCase extends AbstractModelRoundtripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception { expectedSgf=Parser.getSgfData(key); super.setUp(); }
    @Parameterized.Parameters(name = "{0}")
 public static Collection<Object[]> parameters() {
        Set<Object> objects=new LinkedHashSet<>();
        objects.addAll(sgfDataKeySet());
        objects.addAll(sgfFiles());
        return ParameterArray.parameterize(objects);
    }
    public ParameterizedModelRoundTripTestCase(Object key) { this.key=key; }
}
