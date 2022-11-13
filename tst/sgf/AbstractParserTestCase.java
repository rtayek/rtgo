package sgf;
import static org.junit.Assert.assertFalse;
import static sgf.Parser.*;
import static sgf.SgfNode.options;
import static sgf.SgfNode.SgfOptions.*;
import java.io.StringReader;
import org.junit.*;
import utilities.MyTestWatcher;
public abstract class AbstractParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {
        System.out.println("key: "+key);
        expectedSgf=getSgfData(key);
        if(expectedSgf==null) System.out.println("null: "+key);
        else System.out.println("ex before fix: "+expectedSgf);
        //assertNotNull(key.toString(),expectedSgf); 11/8/22 allow for now
        expectedSgf=options.prepareSgf(expectedSgf); // move this stuff to round trip?
        if(options.removeCarriageReturn) if(expectedSgf.contains("\r)")) {
            System.out.println("cr badness");
            System.exit(0);
        }
        if(options.removeLineFeed) if(expectedSgf.contains("\n)")) { System.out.println("lf badness"); System.exit(0); }
        if(containsQuotedControlCharacters(key,expectedSgf))
            expectedSgf=removeQuotedControlCharacters(expectedSgf);
        assertFalse(containsQuotedControlCharacters(key,expectedSgf));
    }
    @Test public void testParse() throws Exception { games=restoreSgf(new StringReader(expectedSgf)); }
    public Object key;
    public String expectedSgf;
    public SgfNode games;
}
