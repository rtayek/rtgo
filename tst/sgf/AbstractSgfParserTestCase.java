package sgf;
import org.junit.*;
public abstract class AbstractSgfParserTestCase extends AbstractSgfKeyedTestCase {
    protected String normalizeExpectedSgf(String rawSgf) {
        return rawSgf;
    }
    private String prepareExpectedSgf(String sgf) {
        return SgfHarness.prepareExpectedSgf(key,sgf);
    }
    @Override protected SgfNode restoreExpectedSgf() {
        return SgfHarness.restoreExpectedSgf(expectedSgf,key);
    }
    @Before public void setUp() throws Exception {
        rawSgf=SgfTestSupport.loadExpectedSgf(key);
        if(rawSgf==null) { expectedSgf=normalizeExpectedSgf(null); return; }
        expectedSgf=normalizeExpectedSgf(rawSgf);
    }
    @Test public void testKey() throws Exception {
        SgfHarness.assertKeyPresent(key,expectedSgf);
        // not currently failing except for one null key.
        // this only happens when all of the model tests are run together.
        //
        // maybe allow as an edge case?
    }
    @Test public void testParse() throws Exception {
        games=SgfHarness.assertParse(key,expectedSgf);
    }
    private void assertFlags(boolean oldFlags) {
        games=SgfHarness.assertFlags(key,expectedSgf,oldFlags);
    }
    @Test public void testHexAscii() {
        SgfHarness.assertHexAscii(key,expectedSgf);
    }
    @Test public void testFlags() {
        assertFlags(true);
    }
    @Test public void testFlagsNew() {
        assertFlags(false);
    }
    public String rawSgf;
    public SgfNode games;
}
