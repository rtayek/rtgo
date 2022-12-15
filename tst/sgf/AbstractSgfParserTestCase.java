package sgf;
import static org.junit.Assert.*;
import static sgf.Parser.*;
import static sgf.SgfNode.SgfOptions.containsQuotedControlCharacters;
import static utilities.Utilities.implies;
import java.io.StringReader;
import org.junit.*;
import io.IO;
import sgf.SgfNode.SgfOptions;
import utilities.MyTestWatcher;
public abstract class AbstractSgfParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    public void prepare() {
        if(expectedSgf!=null) {
            // consolidate so we only have one of these?
            expectedSgf=SgfNode.options.prepareSgf(expectedSgf); // move this stuff to round trip?
            if(SgfNode.options.removeLineFeed) if(expectedSgf.contains("\n)")) {
                System.out.println("lf badness");
                System.exit(0);
            }
            if(containsQuotedControlCharacters(key,expectedSgf)) {
                System.out.println(key+" contains quoted control characters.");
                expectedSgf=SgfOptions.removeQuotedControlCharacters(expectedSgf);
            }
        }
        assertFalse(containsQuotedControlCharacters(key.toString(),expectedSgf));
    }
    @Before public void setUp() throws Exception {
        watcher.key=key;
        if(true) if(key==null) throw new RuntimeException("key: "+key+" is nul!");
        expectedSgf=getSgfData(key);
        if(expectedSgf==null) {
            if(false) throw new RuntimeException("key: "+key+" returns nul!");
            return;
        }
        int p=Parser.parentheses(expectedSgf);
        if(p!=0) { System.out.println(" bad parentheses: "+p); throw new RuntimeException(key+" bad parentheses: "+p); }
        if(alwaysPrepare) prepare();
    }
    @Test public void testKey() throws Exception {
        if(!(key!=null||expectedSgf!=null)) { System.out.println("key!=null||expectedSgf!=null"); IO.stackTrace(10); }
        assertTrue(key!=null||expectedSgf!=null);
        // not currently failing except for one null key.
        // this only happens when all of the model tests are run together.
        //
        // maybe allow as an edge case?
    }
    @Test public void testParse() throws Exception {
        if(expectedSgf!=null) if(expectedSgf.startsWith("(")) {
            if(!expectedSgf.endsWith(")")) System.out.println(key+" does not end with an close parenthesis");
            //fail(key+" does not end with an close parenthesis");
        } else if(!expectedSgf.equals("")) fail(key.toString()+" does not start with an open parenthesis");
        games=expectedSgf!=null?restoreSgf(new StringReader(expectedSgf)):null;
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
        if(expectedSgf!=null) if(expectedSgf.startsWith("(")) {
            if(!expectedSgf.endsWith(")")) System.out.println(key+" sgf does not end with an close parenthesis");
            //fail(key+" does not end with an close parenthesis");
        } else if(!expectedSgf.equals("")) fail(key.toString()+" sgf does not start with an open parenthesis");
        games=expectedSgf!=null?restoreSgf(new StringReader(expectedSgf)):null;
        games.preorderCheck();
    }
    
    boolean alwaysPrepare=false;
    public Object key;
    public String expectedSgf;
    public SgfNode games;
}
