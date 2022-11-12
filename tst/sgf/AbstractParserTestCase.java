package sgf;
import static sgf.Parser.*;
import org.junit.*;
import utilities.MyTestWatcher;
public abstract class AbstractParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {
        expectedSgf=getSgfData(key);
        if(expectedSgf==null) System.out.println("null: "+key);
        //assertNotNull(key.toString(),expectedSgf); 11/8/22 allow for now
        expectedSgf=SgfNode.options.prepareSgf(expectedSgf);
    }
    @Test public void testParse() throws Exception { games=restoreSgf(expectedSgf); }
    public Object key;
    public String expectedSgf;
    public SgfNode games;
}
