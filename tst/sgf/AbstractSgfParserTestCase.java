package sgf;
import io.Logging;
import static org.junit.Assert.*;
import static sgf.SgfNode.SgfOptions.containsQuotedControlCharacters;
import static utilities.Utilities.implies;
import org.junit.*;
import io.IOs;
import sgf.SgfNode.SgfOptions;
public abstract class AbstractSgfParserTestCase extends AbstractSgfKeyedTestCase {
    protected String normalizeExpectedSgf(String rawSgf) {
        return rawSgf;
    }
    protected String prepareExpectedSgf(String sgf) {
        String normalized=sgf;
        if(normalized!=null) {
            // consolidate so we only have one of these?
            normalized=SgfNode.options.prepareSgf(normalized); // move this stuff to round trip?
            if(SgfNode.options.removeLineFeed) if(normalized.contains("\n)")) {
                Logging.mainLogger.info("lf badness");
                System.exit(0);
            }
            if(containsQuotedControlCharacters(key,normalized)) {
                Logging.mainLogger.info(key+" contains quoted control characters.");
                normalized=SgfOptions.removeQuotedControlCharacters(normalized);
            }
        }
        assertFalse(containsQuotedControlCharacters(key.toString(),normalized));
        return normalized;
    }
    @Override protected SgfNode restoreExpectedSgf() {
        SgfTestSupport.assertSgfDelimiters(expectedSgf,key);
        return SgfTestIo.restore(expectedSgf);
    }
    @Before public void setUp() throws Exception {
        rawSgf=SgfTestSupport.loadExpectedSgf(key);
        if(rawSgf==null) { expectedSgf=normalizeExpectedSgf(null); return; }
        expectedSgf=normalizeExpectedSgf(rawSgf);
    }
    @Test public void testKey() throws Exception {
        if(!(key!=null||expectedSgf!=null)) { Logging.mainLogger.info("key!=null||expectedSgf!=null"); IOs.stackTrace(10); }
        assertTrue(key!=null||expectedSgf!=null);
        // not currently failing except for one null key.
        // this only happens when all of the model tests are run together.
        //
        // maybe allow as an edge case?
    }
    @Test public void testParse() throws Exception {
        games=restoreExpectedSgf();
    }
    private void assertFlags(boolean oldFlags) {
        games=restoreExpectedSgf();
        if(games==null) return;
        if(oldFlags) games.oldPreorderCheckFlags();
        else games.preorderCheckFlags();
    }
    @Test public void testHexAscii() {
        String encoded=expectedSgf!=null?HexAscii.encode(expectedSgf.getBytes()):null;
        String actualSgf=encoded!=null?HexAscii.decodeToString(encoded):null;
        String keyString=key!=null?key.toString():null;
        assertTrue(keyString,implies(expectedSgf==null,encoded==null));
        assertTrue(keyString,implies(encoded==null,actualSgf==null));
        assertEquals(keyString,expectedSgf,actualSgf);
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
