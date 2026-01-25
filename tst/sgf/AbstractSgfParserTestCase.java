package sgf;
import org.junit.*;
import org.junit.runners.Parameterized;
import utilities.TestKeys;
import utilities.TestSupport;
public abstract class AbstractSgfParserTestCase extends TestSupport {
    @Parameterized.Parameter public Object key;
    protected String expectedSgf;
    protected Object defaultKey() {
        return null;
    }

    protected final void ensureKey() {
        if(key!=null) return;
        Object fallback=defaultKey();
        if(fallback==null) fallback=TestKeys.sgfExampleFromRedBean;
        if(fallback!=null) key=fallback;
    }

    protected String normalizeExpectedSgf(String rawSgf) {
        return rawSgf;
    }
    protected SgfNode restoreExpectedSgf() {
        return SgfHarness.restoreExpectedSgf(expectedSgf,key);
    }
    protected MNode restoreExpectedMNode() {
        return SgfHarness.restoreMNode(expectedSgf);
    }
    @Before public void setUp() throws Exception {
        ensureKey();
        watcher.key=key;
        rawInput=false;
        if(key instanceof SgfHarness.RawSgf raw) {
            rawSgf=raw.sgf();
            rawInput=true;
        } else {
            rawSgf=SgfHarness.loadExpectedSgf(key);
        }
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
        games=SgfHarness.assertParse(key,expectedSgf,!rawInput);
    }
    private void assertFlags(boolean oldFlags) {
        games=SgfHarness.assertFlags(key,expectedSgf,oldFlags,!rawInput);
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
    protected boolean rawInput;
}

