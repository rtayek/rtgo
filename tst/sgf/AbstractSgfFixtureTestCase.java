package sgf;
import org.junit.Before;
import org.junit.Rule;
import utilities.MyTestWatcher;
abstract class AbstractSgfFixtureTestCase {
    @Before public void setUp() throws Exception {
        watcher.key=key;
        expectedSgf=SgfTestSupport.loadExpectedSgf(key);
        if(expectedSgf==null) { return; }
    }
    void assertSgfDelimiters() {
        SgfTestSupport.assertSgfDelimiters(expectedSgf,key);
    }
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    Object key;
    String expectedSgf;
}
