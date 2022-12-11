package model;
import org.junit.*;
import sgf.Parser;
import utilities.MyTestWatcher;
public class ModelTestCase extends AbstractModelTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Override @Before public void setUp() throws Exception {
        key=Parser.empty;
        key="sgfExamleFromRedBean";
        //key=null;
        super.setUp();
    }
}
