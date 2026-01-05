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
        String actual=modelRoundTripToString(expected);
        try {
            r.close();
        } catch(IOException e) {
            Logging.mainLogger.severe("caught: "+e);
        }
        assertEquals(expected,actual);
    }
    private static String modelRoundTripToString(Reader reader) {
        return ModelTestIo.modelRoundTripToString(reader);
    }
    private static String modelRoundTripToString(String sgf) {
        return ModelTestIo.modelRoundTripToString(sgf);
    }
    @Test public void testOneMove() throws IOException {
        String sgfString=getSgfData("oneMoveAtA1");
        checkString(sgfString);
        //for(String filename:ParserTestCase.filenames)
        //checkFile(new File(filename));
    }
    @Test public void testTwoEmptyWithSemicolon() throws IOException {
        String sgfString=getSgfData("twoEmptyWithSemicolon");
        checkString(sgfString);
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
        String expected=modelRoundTripToString(sgf);
        String actual=modelRoundTripToString(expected);
        assertEquals(expected,actual);
    }
}
