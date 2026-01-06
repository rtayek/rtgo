package model;
import org.junit.Rule;
import utilities.MyTestWatcher;
import utilities.TestKeys;
public class ModelRoundYtipTestCase extends AbstractModelRoundtripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    public ModelRoundYtipTestCase() {
        key=TestKeys.sgfExampleFromRedBean;
    }
}
