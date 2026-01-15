package sgf;
import static org.junit.Assert.assertEquals;
import static sgf.Parser.getSgfData;
import static utilities.Utilities.fromFile;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import org.junit.*;
import io.*;
import io.IOs;
import model.ModelTestIo;
import utilities.MyTestWatcher;
import utilities.TestKeys;
// sgf file->sgf node tree->sgf tree->sgf string
public class RoundTripTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    // maybe add all of the longer files
    // add the short ones to the map in parser.
    // these are probably duplicates
    public static void checkReader(Reader r) throws IOException {
        // looks like a double round trup?
        // do we need this?
        String expected=modelRoundTripToString(r);
        assertRoundTripTwice(expected);
        try {
            r.close();
        } catch(IOException e) {
            Logging.mainLogger.severe("caught: "+e);
        }
    }
    private static void assertRoundTripTwice(String sgf) {
        String expected=modelRoundTripToString(sgf);
        String actual=modelRoundTripToString(expected);
        assertEquals(expected,actual);
    }
    private static String modelRoundTripToString(Reader reader) {
        return ModelTestIo.modelRoundTripToString(reader);
    }
    private static String modelRoundTripToString(String sgf) {
        return ModelTestIo.modelRoundTripToString(sgf);
    }
    private static void checkKey(String key) throws IOException {
        checkString(getSgfData(key));
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
        checkReader(IOs.toReader(file));
        StringBuffer sb=new StringBuffer();
        fromFile(sb,file);
        // now what should i do with string buffer?
        //
    }
    private static void checkString(String sgf) throws IOException {
        assertRoundTripTwice(sgf);
    }
}
