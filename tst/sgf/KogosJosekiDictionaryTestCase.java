package sgf;
import static io.Logging.parserLogger;
import static org.junit.Assert.assertTrue;
import java.io.File;
import org.junit.*;
import io.Logging;
import sgf.combine.Combine;
@Ignore public class KogosJosekiDictionaryTestCase extends AbstractIgnoreMoveAndSetupFlagsTestCase {
    @Test public void testFiles() throws Exception {
        boolean fail=false;
        for(String filename:filenames) try {
            File file=new File(Combine.pathToHere,filename);
            boolean ok=SgfTestSupport.roundTripTwice(file);
            if(!ok) Logging.mainLogger.info(file+" fails!");
            fail|=!ok;
        } catch(Exception e) {
            parserLogger.warning(this+" caught: "+e);
            fail|=true;
        }
        if(fail) assertTrue(false);
    }
    boolean fail=true;
    static String[] filenames=new String[] {"KogosJosekiDictionary.sgf"};
}
