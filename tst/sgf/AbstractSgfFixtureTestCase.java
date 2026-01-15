package sgf;
import org.junit.Before;
abstract class AbstractSgfFixtureTestCase extends AbstractSgfKeyedTestCase {
    @Before public void setUp() throws Exception {
        ensureKey();
        watcher.key=key;
        expectedSgf=SgfTestSupport.loadExpectedSgf(key);
        if(expectedSgf==null) { return; }
    }
    void assertSgfDelimiters() {
        SgfTestSupport.assertSgfDelimiters(expectedSgf,key);
    }
}
