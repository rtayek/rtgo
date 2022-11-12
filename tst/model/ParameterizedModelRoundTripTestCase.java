package model;
import java.util.Collection;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import sgf.Parser;
import utilities.*;
@RunWith(Parameterized.class) public class ParameterizedModelRoundTripTestCase extends AbstractModelRoundtripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception { expectedSgf=Parser.getSgfData(key); }
    @Parameters public static Collection<Object[]> parameters() {
        return ParameterArray.parameterize(Parser.sgfDataKeySet());
    }
    public ParameterizedModelRoundTripTestCase(Object key) { this.key=key; }
}
