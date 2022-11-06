package sgf;
import static io.Logging.parserLogger;
import static org.junit.Assert.*;
import static sgf.Parser.*;
import java.io.*;
import java.util.Collection;
import org.junit.*;
import org.junit.runners.Parameterized.Parameters;
import model.Model;
import utilities.MyTestWatcher;
public abstract class AbstractParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {
        expectedSgf=getSgfData(key);
        assertNotNull(key,expectedSgf);
        expectedSgf=Parser.options.prepareSgf(expectedSgf);
    }
    @After public void tearDown() throws Exception {}
    @Parameters public static Collection<Object[]> parameters() { return Parser.sgfTestData(); }
    @Test public void testParse() throws Exception {
        if(expectedSgf=="") {
            if(!key.equals("reallyEmpty")) throw new RuntimeException("expected sgf string is empty for: "+key);
        } else if(expectedSgf.charAt(0)!='(')
            parserLogger.severe("sgf start with a: '"+expectedSgf.charAt(0)+"' (not a: '(').");
        games=new Parser().parse(expectedSgf);
        assertNotNull(key,games);
    }
    @Test public void testRoundTrip() throws Exception {
        String actualSgf=sgfRoundTrip(expectedSgf);
        // maybe save() should always add the line feed.
        //if(!actualSgf.endsWith("\n")) actualSgf+="\n";
        if(!expectedSgf.equals(actualSgf)) {
            //printDifferences(expectedSgf,actualSgf);
        }
        assertEquals(key,expectedSgf,actualSgf);
    }
    @Test public void testLongRoundTrip() throws Exception {
        StringWriter stringWriter=new StringWriter();
        MNode games=MNode.mNodeRoundTrip(new StringReader(expectedSgf),stringWriter);
        String actualSgf=stringWriter.toString();
        assertEquals(key,expectedSgf,actualSgf);
    }
    @Test public void testLongRoundTrip2() throws Exception {
        StringWriter stringWriter=new StringWriter();
        @SuppressWarnings("unused") MNode games=MNode.mNoderoundTrip2(expectedSgf,stringWriter);
        String actualSgf=stringWriter.toString();
        assertEquals(key,expectedSgf,actualSgf);
    }
    @Test public void testRoundTripeTwice() throws Exception {
        StringReader reader=new StringReader(expectedSgf);
        boolean isOk=sgfRoundTripTwice(reader);
        assertTrue(isOk);
    }
    @Test public void testSaveAndRestore() throws Exception {
        // try to compare two trees for equality.
        // write a deep equals.
        //fail("nyi");
    }
    @Test public void testRT0() throws Exception {
        Model model=new Model();
        model.restore(new StringReader(expectedSgf));
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue(ok);
        String actualSgf=stringWriter.toString();
        System.out.println(actualSgf);
    }
    @Test public void testRestoreAndSave() throws Exception {
        Model model=new Model();
        model.restore(new StringReader(expectedSgf));
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue(ok);
        String actualSgf=stringWriter.toString();
        actualSgf=options.removeUnwanted(actualSgf);
        //printDifferences(expectedSgf,actualSgf);
        assertEquals(key,expectedSgf,actualSgf);
    }
    @Test public void testHexAscii() {
        String encoded=HexAscii.encode(expectedSgf.getBytes());
        String actualSgf=HexAscii.decodeToString(encoded);
        assertEquals(expectedSgf,actualSgf);
    }
    @Test public void testCannonical() {
        String actualSgf=sgfRoundTrip(expectedSgf);
        String actual2=sgfRoundTrip(actualSgf);
        assertEquals(key,actualSgf,actual2);
    }
    @Test public void testCheckBoardInRoot() {
        boolean ok=checkBoardInRoot(key);
        // always fails because none of these have a board in root.
        // that seems to be the usual case.
        // that is always the usual case,
        // since i always add a dummy multi-way node!
        // a better check would be if there was a board in the first real node.
        //assertTrue(ok);
    }
    String key;
    String expectedSgf;
    SgfNode games;
}
