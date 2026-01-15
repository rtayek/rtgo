package sgf;
import static org.junit.Assert.fail;
import static sgf.Parser.getSgfData;
import org.junit.Before;
import org.junit.Rule;
import io.Logging;
import utilities.MyTestWatcher;
abstract class AbstractSgfFixtureTestCase {
    @Before public void setUp() throws Exception {
        watcher.key=key;
        if(key==null) throw new RuntimeException("key: "+key+" is nul!");
        expectedSgf=getSgfData(key);
        if(expectedSgf==null) { return; }
        int p=Parser.parentheses(expectedSgf);
        if(p!=0) { Logging.mainLogger.info(" bad parentheses: "+p); throw new RuntimeException(key+" bad parentheses: "+p); }
    }
    void assertSgfDelimiters() {
        if(expectedSgf!=null) if(expectedSgf.startsWith("(")) {
            if(!expectedSgf.endsWith(")")) Logging.mainLogger.info(key+" does not end with an close parenthesis");
        } else if(!expectedSgf.equals("")) fail(key.toString()+" does not start with an open parenthesis");
    }
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    Object key;
    String expectedSgf;
}
