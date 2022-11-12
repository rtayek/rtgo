package sgf;
import java.util.Collection;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import model.AbstractModelTestCase;
import utilities.*;
@RunWith(Parameterized.class) public class ParameterizedMNodeTestCaseMNodeTestCase extends AbstractModelTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> parameters() {
        return ParameterArray.parameterize(Parser.sgfDataKeySet());
    }
    public ParameterizedMNodeTestCaseMNodeTestCase(String key) { this.key=key; }
}
