package sgf;
import io.Logging;
import static org.junit.Assert.*;
import static sgf.Parser.*;
import static sgf.SgfNode.SgfOptions.containsQuotedControlCharacters;
import static utilities.Utilities.implies;
import org.junit.*;
import io.IOs;
import sgf.SgfNode.SgfOptions;
import utilities.MyTestWatcher;
public abstract class AbstractSgfParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
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
    private void assertSgfDelimiters() {
        if(expectedSgf!=null) if(expectedSgf.startsWith("(")) {
            if(!expectedSgf.endsWith(")")) Logging.mainLogger.info(key+" does not end with an close parenthesis");
            //fail(key+" does not end with an close parenthesis");
        } else if(!expectedSgf.equals("")) fail(key.toString()+" does not start with an open parenthesis");
    }
    private SgfNode parseGames() {
        assertSgfDelimiters();
        return SgfTestIo.restore(expectedSgf);
    }
    @Before public void setUp() throws Exception {
        watcher.key=key;
        if(true) if(key==null) throw new RuntimeException("key: "+key+" is nul!");
        rawSgf=getSgfData(key);
        if(rawSgf==null) { if(false) throw new RuntimeException("key: "+key+" returns nul!"); expectedSgf=normalizeExpectedSgf(null); return; }
        int p=Parser.parentheses(rawSgf);
        if(p!=0) { Logging.mainLogger.info(" bad parentheses: "+p); throw new RuntimeException(key+" bad parentheses: "+p); }
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
        games=parseGames();
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
        games=parseGames();
        if(games!=null) games.oldPreorderCheckFlags();
    }
    @Test public void testFlagsNew() {
        games=parseGames();
        if(games!=null) games.preorderCheckFlags();
    }
    public Object key;
    public String rawSgf;
    public String expectedSgf;
    public SgfNode games;
}
