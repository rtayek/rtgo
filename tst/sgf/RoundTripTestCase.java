package sgf;
import static utilities.Utilities.fromFile;
import java.io.File;
import java.io.IOException;
import org.junit.Test;
import io.IOs;
import utilities.TestKeys;
// sgf file->sgf node tree->sgf tree->sgf string
public class RoundTripTestCase extends AbstractWatchedTestCase {
    // maybe add all of the longer files
    // add the short ones to the map in parser.
    // these are probably duplicates
    private static void checkKeys(String... keys) throws IOException {
        for(String key:keys) checkString(SgfTestSupport.loadExpectedSgf(key));
    }
    @Test public void testBasicKeys() throws IOException {
        checkKeys(TestKeys.oneMoveAtA1,TestKeys.twoEmptyWithSemicolon);
    }
    @Test public void testVariation() throws IOException {
        File file=new File(Parser.sgfPath,"variation.sgf");
        SgfTestSupport.assertModelRoundTripTwice(IOs.toReader(file));
        StringBuffer sb=new StringBuffer();
        fromFile(sb,file);
        // now what should i do with string buffer?
        //
    }
    private static void checkString(String sgf) throws IOException {
        SgfTestSupport.assertModelRoundTripTwice(sgf);
    }
}
