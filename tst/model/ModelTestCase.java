package model;
import java.util.Collection;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import sgf.Parser;
import utilities.*;
@RunWith(Parameterized.class) public class ModelTestCase extends AbstractModelTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> parameters() {
        return ParameterArray.parameterize(Parser.sgfDataKeySet());
    }
    public ModelTestCase(String key) { this.key=key; }
}
