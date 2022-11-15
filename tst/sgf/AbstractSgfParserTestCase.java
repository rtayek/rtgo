package sgf;
import static org.junit.Assert.*;
import static sgf.Parser.*;
import static sgf.SgfNode.options;
import static sgf.SgfNode.SgfOptions.*;
import java.io.StringReader;
import org.junit.*;
import utilities.MyTestWatcher;
public abstract class AbstractSgfParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {
        System.out.println("key: "+key);
        watcher.key=key;
        expectedSgf=getSgfData(key);
        if(expectedSgf==null) return;
        else; //System.out.println("ex before fix: "+expectedSgf);
        //assertNotNull(key.toString(),expectedSgf); 11/8/22 allow for now
        expectedSgf=options.prepareSgf(expectedSgf); // move this stuff to round trip?
        if(options.removeLineFeed) if(expectedSgf.contains("\n)")) { System.out.println("lf badness"); System.exit(0); }
        if(containsQuotedControlCharacters(key,expectedSgf)) expectedSgf=removeQuotedControlCharacters(expectedSgf);
        assertFalse(containsQuotedControlCharacters(key,expectedSgf));
    }
    @Test public void testParse() throws Exception {
        if(expectedSgf!=null) if(expectedSgf.startsWith("(")) {
            if(!expectedSgf.endsWith(")")) fail(key+" does not end with a )");
        } else if(!expectedSgf.equals("")) fail(key+" does not start with a (");
        games=expectedSgf!=null?restoreSgf(new StringReader(expectedSgf)):null;
    }
    public Object key;
    public String expectedSgf;
    public SgfNode games;
}
