package sgf;
import org.junit.Before;
import org.junit.Rule;
import utilities.MyTestWatcher;
import org.junit.runners.Parameterized;
abstract class AbstractSgfFixtureTestCase {
    @Before public void setUp() throws Exception {
        watcher.key=key;
        expectedSgf=SgfTestSupport.loadExpectedSgf(key);
        if(expectedSgf==null) { return; }
    }
    void assertSgfDelimiters() {
        SgfTestSupport.assertSgfDelimiters(expectedSgf,key);
    }
    SgfNode restoreExpectedSgf() {
        return SgfTestIo.restore(expectedSgf);
    }
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameterized.Parameter public Object key;
    String expectedSgf;
}
