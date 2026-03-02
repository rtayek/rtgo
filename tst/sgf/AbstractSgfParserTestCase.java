package sgf;
import utilities.MyTestWatcher;
import com.tayek.util.io.FileIO;
import org.junit.*;
import org.junit.runners.Parameterized;
import utilities.TestKeys;
public abstract class AbstractSgfParserTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
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
    @Before public void setUp() throws Exception {
        ensureKey();
        watcher.key=key;
        rawInput=false;
        if(key instanceof SgfTestHarness.RawSgf raw) {
            rawSgf=raw.sgf();
            rawInput=true;
        } else {
            rawSgf=SgfIo.loadExpectedSgf(key);
        }
        if(rawSgf==null) { expectedSgf=normalizeExpectedSgf(null); return; }
        expectedSgf=normalizeExpectedSgf(rawSgf);
    }
    @Test public void testKey() throws Exception {
        SgfTestHarness.assertKeyPresent(key,expectedSgf);
        // not currently failing except for one null key.
        // this only happens when all of the model tests are run together.
        //
        // maybe allow as an edge case?
    }
    @Test public void testParse() throws Exception {
        games=SgfTestHarness.restoreExpectedSgf(expectedSgf,key,!rawInput);
    }
    private void assertFlags(boolean oldFlags) {
        games=SgfTestHarness.assertFlags(key,expectedSgf,oldFlags,!rawInput);
    }
    @Test public void testHexAscii() {
        SgfTestHarness.assertHexAscii(key,expectedSgf);
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



