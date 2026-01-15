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
    private static void checkKey(String key) throws IOException {
        checkString(SgfTestSupport.loadExpectedSgf(key));
    }
    @Test public void testOneMove() throws IOException {
        checkKey(TestKeys.oneMoveAtA1);
        //for(String filename:ParserTestCase.filenames)
        //checkFile(new File(filename));
    }
    @Test public void testTwoEmptyWithSemicolon() throws IOException {
        checkKey(TestKeys.twoEmptyWithSemicolon);
        //for(String filename:ParserTestCase.filenames)
        //checkFile(new File(filename));
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
