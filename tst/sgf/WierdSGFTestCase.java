package sgf;
import io.Logging;
import io.IOs;
import static io.Logging.parserLogger;
import static org.junit.Assert.fail;
import static sgf.Parser.restoreSgf;
import static utilities.Utilities.addFiles;
import java.io.*;
import java.util.*;
import org.junit.Ignore;
import org.junit.Test;
@Ignore public class WierdSGFTestCase extends AbstractWatchedTestCase {
    // these take a long time when tst/ is run
    // check to see if they take a long time when run by themselves.
    // they do not take a long time.
    // they mumble a lot, so ignoring for now.
    // parameterize this!
    private static List<File> loadStrangeFiles() {
        if(!strange.exists()) fail(strange+" does not exits!");
        List<File> files=addFiles(null,strange);
        if(files.size()==0) fail("no files!");
        return files;
    }
    @Test public void testWierd() throws Exception {
        List<File> files=loadStrangeFiles();
        fail=false;
        for(File file:files) try {
            SgfTestSupport.roundTripTwiceWithLogging(file);
        } catch(Exception e) {
            parserLogger.warning(this+" caught: "+e);
        }
    }
    @Test public void testFirstNodeOfWierd() throws Exception {
        List<File> files=loadStrangeFiles();
        fail=false;
        List<SgfNode> all=new ArrayList<>();
        for(File file:files) try {
            SgfNode games=restoreSgf(IOs.toReader(file));
            all.add(games);
        } catch(Exception e) {
            Logging.mainLogger.info(this+" caught: "+e); //
        }
        parserLogger.warning(all.size()+" games in "+strange);
    }
    boolean fail=true;
    static final File strange=new File("strangesgf/");
}
