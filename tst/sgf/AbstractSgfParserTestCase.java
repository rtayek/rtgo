package sgf;
import static org.junit.Assert.*;
import static sgf.Parser.*;
import static sgf.SgfNode.SgfOptions.containsQuotedControlCharacters;
import static utilities.Utilities.implies;
import java.io.StringReader;
import org.junit.*;
import sgf.SgfNode.SgfOptions;
import utilities.MyTestWatcher;
public abstract class AbstractSgfParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    public void prepare() {
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
        assertFalse(containsQuotedControlCharacters(key.toString(),expectedSgf));
    }
    @Before public void setUp() throws Exception {
        //System.out.println("key: "+key);
        watcher.key=key;
        expectedSgf=getSgfData(key);
        if(expectedSgf==null) return;
        else; //System.out.println("ex before fix: "+expectedSgf);
        //assertNotNull(key.toString(),expectedSgf); 11/8/22 allow for now
        if(alwaysPrepare) prepare();
    }
    @Test public void testParse() throws Exception {
        if(expectedSgf!=null) if(expectedSgf.startsWith("(")) {
            if(!expectedSgf.endsWith(")"))
                System.out.println(key+" does not end with an close parenthesis");
            //fail(key+" does not end with an close parenthesis");
        } else if(!expectedSgf.equals("")) fail(key.toString()+" does not start with an open parenthesis");
        games=expectedSgf!=null?restoreSgf(new StringReader(expectedSgf)):null;
    }
    @Test public void testHexAscii() {
        String encoded=expectedSgf!=null?HexAscii.encode(expectedSgf.getBytes()):null;
        String actualSgf=encoded!=null?HexAscii.decodeToString(encoded):null;
        assertTrue(key.toString(),implies(expectedSgf==null,encoded==null));
        assertTrue(key.toString(),implies(encoded==null,actualSgf==null));
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    
    boolean alwaysPrepare=false;
    public Object key;
    public String expectedSgf;
    public SgfNode games;
}
