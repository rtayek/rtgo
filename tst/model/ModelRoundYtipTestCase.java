package model;
import org.junit.*;
import sgf.Parser;
import utilities.MyTestWatcher;
public class ModelRoundYtipTestCase extends AbstractModelRoundtripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception { key=Parser.empty; super.setUp(); }
}
