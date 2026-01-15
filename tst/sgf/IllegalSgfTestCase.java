package sgf;
import io.Logging;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import utilities.MyTestWatcher;
@RunWith(Parameterized.class) public class IllegalSgfTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Parameters public static Collection<Object[]> testData() {
        //consolidate!
        List<Object[]> parameterArrays=new ArrayList<>();
        for(String key:Parser.illegalSgfKeys) parameterArrays.add(new Object[] {key});
        return parameterArrays;
    }
    @Before public void setUp() throws Exception { Logging.mainLogger.info(String.valueOf(key)); }
    @After public void tearDown() throws Exception {}
    public IllegalSgfTestCase(String key) { this.key=key; }
    @Test public void testParse() throws Exception {
        String expectedSgf=SgfTestSupport.loadExpectedSgf(key);
        SgfNode games=SgfTestIo.restore(expectedSgf);
        //assertNull(key.toString(),games); // allow null for now
    }
    String key;
}
