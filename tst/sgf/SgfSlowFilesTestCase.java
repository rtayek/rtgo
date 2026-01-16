package sgf;

import io.Logging;
import io.IOs;
import static io.Logging.parserLogger;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static sgf.Parser.restoreSgf;
import static utilities.Utilities.addFiles;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import sgf.combine.Combine;

@Ignore public class SgfSlowFilesTestCase extends AbstractWatchedTestCase {
    private boolean oldIgnoreFlags;

    @Before public void setUpIgnoreMoveAndSetupFlags() throws Exception {
        oldIgnoreFlags=SgfNode.ignoreMoveAndSetupFlags;
        SgfNode.ignoreMoveAndSetupFlags=true;
    }

    @After public void tearDownIgnoreMoveAndSetupFlags() throws Exception {
        SgfNode.ignoreMoveAndSetupFlags=oldIgnoreFlags;
    }

    @Test public void testKogosJosekiDictionary() throws Exception {
        File file=SgfTestSupport.firstExistingFile(
                new File(Combine.pathToHere,kogoFilename),
                new File("sgf",kogoFilename)
        );
        if(file==null) {
            assertTrue(kogoFilename+" not found",false);
            return;
        }
        boolean ok=SgfTestSupport.roundTripTwiceWithLogging(file);
        assertTrue(ok);
    }

    @Test public void testWierd() throws Exception {
        List<File> files=loadStrangeFiles();
        failFast=false;
        for(File file:files) try {
            SgfTestSupport.roundTripTwiceWithLogging(file);
        } catch(Exception e) {
            parserLogger.warning(this+" caught: "+e);
        }
    }

    @Test public void testFirstNodeOfWierd() throws Exception {
        List<File> files=loadStrangeFiles();
        failFast=false;
        List<SgfNode> all=new ArrayList<>();
        for(File file:files) try {
            SgfNode games=restoreSgf(IOs.toReader(file));
            all.add(games);
        } catch(Exception e) {
            Logging.mainLogger.info(this+" caught: "+e); //
        }
        parserLogger.warning(all.size()+" games in "+strangeDir);
    }

    private static List<File> loadStrangeFiles() {
        if(!strangeDir.exists()) fail(strangeDir+" does not exits!");
        List<File> files=addFiles(null,strangeDir);
        if(files.size()==0) fail("no files!");
        return files;
    }

    boolean failFast=true;
    static final File strangeDir=new File("strangesgf/");
    static final String kogoFilename="KogosJosekiDictionary.sgf";
}
