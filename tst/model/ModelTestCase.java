package model;
import org.junit.Rule;
import utilities.MyTestWatcher;
import utilities.TestKeys;
public class ModelTestCase extends AbstractModelTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    public ModelTestCase() {
        key=TestKeys.sgfExampleFromRedBean;
    }
}
