package sgf;
import static org.junit.Assert.*;
import static sgf.Parser.*;
import static sgf.SgfNode.*;
import java.io.StringReader;
import org.junit.*;
import utilities.MyTestWatcher;
public abstract class AbstractParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {
        System.out.println(key);
        expectedSgf=getSgfData(key);
        if(expectedSgf==null) System.out.println("null: "+key);
        //assertNotNull(key.toString(),expectedSgf); 11/8/22 allow for now
        expectedSgf=SgfNode.options.prepareSgf(expectedSgf);
    }
    @After public void tearDown() throws Exception {}
    @Test public void testParse() throws Exception {
        games=restoreSgf(expectedSgf);
        //assertNotNull(key.toString(),games);
        // allow null for now (11/8/22).
    }
    @Test public void testRoundTrip() throws Exception {
        String actualSgf=sgfRoundTrip(expectedSgf);
        // maybe save() should always add the line feed.
        //if(!actualSgf.endsWith("\n")) actualSgf+="\n";
        if(expectedSgf!=null) if(!expectedSgf.equals(actualSgf)) {
            //printDifferences(expectedSgf,actualSgf);
        }
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testRoundTripeTwice() throws Exception {
        StringReader reader=expectedSgf!=null?new StringReader(expectedSgf):null;
        boolean isOk=sgfRoundTripTwice(reader);
        assertTrue(isOk);
    }
    @Test public void testSaveAndRestore() throws Exception {
        // do a restore, the a round trip?
        // try to compare two trees for equality.
        // write a deep equals.
        //fail("nyi");
    }
    @Test public void testHexAscii() {
        String encoded=expectedSgf!=null?HexAscii.encode(expectedSgf.getBytes()):null;
        String actualSgf=encoded!=null?HexAscii.decodeToString(encoded):null;
        assertEquals(expectedSgf,actualSgf);
    }
    @Test public void testCannonical() {
        String actualSgf=sgfRoundTrip(expectedSgf);
        String actual2=sgfRoundTrip(actualSgf);
        assertEquals(key.toString(),actualSgf,actual2);
    }
    public Object key;
    public String expectedSgf;
    public SgfNode games;
}
