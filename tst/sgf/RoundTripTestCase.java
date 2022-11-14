package sgf;
import static org.junit.Assert.assertEquals;
import static sgf.Parser.getSgfData;
import static utilities.Utilities.fromFile;
import java.io.*;
import org.junit.*;
import io.*;
import model.Model;
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
        StringWriter stringWriter=new StringWriter();
        @SuppressWarnings("unused") MNode games=Model.modelRoundTrip(r,stringWriter);
        String expected=stringWriter.toString();
        Reader reader=new StringReader(expected);
        stringWriter=new StringWriter();
        games=Model.modelRoundTrip(reader,stringWriter);
        String actual=stringWriter.toString();
        try {
            r.close();
        } catch(IOException e) {
            Logging.mainLogger.severe("caught: "+e);
        }
        assertEquals(expected,actual);
    }
    @Test public void testOneMove() throws IOException {
        String sgfString=getSgfData("oneMoveAtA1");
        checkReader(new StringReader(sgfString));
        //for(String filename:ParserTestCase.filenames)
        //checkFile(new File(filename));
    }
    @Test public void testTwoEmptyWithSemicolon() throws IOException {
        String sgfString=getSgfData("twoEmptyWithSemicolon");
        checkReader(new StringReader(sgfString));
        //for(String filename:ParserTestCase.filenames)
        //checkFile(new File(filename));
    }
    @Test public void testVariation() throws IOException {
        File file=new File("sgf","variation.sgf");
        checkReader(IO.toReader(file));
        StringBuffer sb=new StringBuffer();
        fromFile(sb,file);
        // now what should i do with string buffer?
        //
    }
}
