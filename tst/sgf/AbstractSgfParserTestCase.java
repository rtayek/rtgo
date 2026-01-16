package sgf;
import org.junit.*;
public abstract class AbstractSgfParserTestCase extends AbstractSgfKeyedTestCase {
    protected String normalizeExpectedSgf(String rawSgf) {
        return rawSgf;
    }
    private String prepareExpectedSgf(String sgf) {
        return SgfParserHarness.prepareExpectedSgf(key,sgf);
    }
    @Override protected SgfNode restoreExpectedSgf() {
        return SgfParserHarness.restoreExpectedSgf(expectedSgf,key);
    }
    @Before public void setUp() throws Exception {
        rawSgf=SgfTestSupport.loadExpectedSgf(key);
        if(rawSgf==null) { expectedSgf=normalizeExpectedSgf(null); return; }
        expectedSgf=normalizeExpectedSgf(rawSgf);
    }
    @Test public void testKey() throws Exception {
        SgfParserHarness.assertKeyPresent(key,expectedSgf);
        // not currently failing except for one null key.
        // this only happens when all of the model tests are run together.
        //
        // maybe allow as an edge case?
    }
    @Test public void testParse() throws Exception {
        games=SgfParserHarness.assertParse(key,expectedSgf);
    }
    private void assertFlags(boolean oldFlags) {
        games=SgfParserHarness.assertFlags(key,expectedSgf,oldFlags);
    }
    @Test public void testHexAscii() {
        SgfParserHarness.assertHexAscii(key,expectedSgf);
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
