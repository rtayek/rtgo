package sgf;
import static io.Logging.parserLogger;
import static org.junit.Assert.assertTrue;
import java.io.File;
import org.junit.*;
import io.IO;
import sgf.combine.Combine;
@Ignore public class KogosJosekiDictionaryTestCase {
    @Before public void setUp() throws Exception {
        old=SgfNode.ignoreMoveAndSetupFlags;
        SgfNode.ignoreMoveAndSetupFlags=true;
    }
    @After public void tearDown() throws Exception { SgfNode.ignoreMoveAndSetupFlags=old; }
    @Test public void testFiles() throws Exception {
        boolean fail=false;
        for(String filename:filenames) try {
            File file=new File(Combine.pathToHere,filename);
            boolean ok=Parser.sgfRoundTripTwice(IO.toReader(file));
            System.out.println(file+" fails!");
            fail|=!ok;
        } catch(Exception e) {
            parserLogger.warning(this+" caught: "+e);
            fail|=true;
        }
        if(fail) assertTrue(false);
    }
    boolean fail=true;
    boolean old;
    static String[] filenames=new String[] {"KogosJosekiDictionary.sgf"};
}
