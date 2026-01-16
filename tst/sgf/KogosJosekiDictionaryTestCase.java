package sgf;
import static org.junit.Assert.assertTrue;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import sgf.combine.Combine;
@Ignore public class KogosJosekiDictionaryTestCase extends AbstractWatchedTestCase {
    private boolean oldIgnoreFlags;

    @Before public void setUpIgnoreMoveAndSetupFlags() throws Exception {
        oldIgnoreFlags=SgfNode.ignoreMoveAndSetupFlags;
        SgfNode.ignoreMoveAndSetupFlags=true;
    }

    @After public void tearDownIgnoreMoveAndSetupFlags() throws Exception {
        SgfNode.ignoreMoveAndSetupFlags=oldIgnoreFlags;
    }

    @Test public void testFiles() throws Exception {
        File file=SgfTestSupport.firstExistingFile(
                new File(Combine.pathToHere,filename),
                new File("sgf",filename)
        );
        if(file==null) {
            assertTrue(filename+" not found",false);
            return;
        }
        boolean ok=SgfTestSupport.roundTripTwiceWithLogging(file);
        assertTrue(ok);
    }
    static final String filename="KogosJosekiDictionary.sgf";
}
