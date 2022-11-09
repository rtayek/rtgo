package sgf;
import static org.junit.Assert.*;
import static sgf.Parser.*;
import java.io.*;
import org.junit.*;
import model.Model;
import utilities.MyTestWatcher;
public abstract class AbstractParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {
        System.out.println(key);
        expectedSgf=getSgfData(key);
        if(expectedSgf==null) System.out.println("null: "+key);
        //assertNotNull(key.toString(),expectedSgf); 11/8/22 allow for now
        expectedSgf=Parser.options.prepareSgf(expectedSgf);
    }
    @After public void tearDown() throws Exception {}
    @Test public void testParse() throws Exception {
        //if(expectedSgf=="") {
        //    if(!key.equals("reallyEmpty")) throw new RuntimeException("expected sgf string is empty for: "+key);
        //} else if(expectedSgf.charAt(0)!='(')
        //    parserLogger.severe("sgf start with a: '"+expectedSgf.charAt(0)+"' (not a: '(').");
        games=new Parser().parse(expectedSgf);
        //assertNotNull(key.toString(),games);
        // allow null for now (11/8/22).
    }
    @Test public void testRoundTrip() throws Exception {
        String actualSgf=sgfRoundTrip(expectedSgf);
        // maybe save() should always add the line feed.
        //if(!actualSgf.endsWith("\n")) actualSgf+="\n";
        if(expectedSgf!=null) if(!expectedSgf.equals(actualSgf)) {
            //printDifferences(expectedSgf,actualSgf);
        }
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testLongRoundTrip() throws Exception {
        StringWriter stringWriter=new StringWriter();
        MNode games=MNode.mNodeRoundTrip(expectedSgf!=null?new StringReader(expectedSgf):null,stringWriter);
        String actualSgf=expectedSgf!=null?stringWriter.toString():null;
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testLongRoundTrip2() throws Exception {
        StringWriter stringWriter=new StringWriter();
        @SuppressWarnings("unused") MNode games=MNode.mNoderoundTrip2(expectedSgf,stringWriter);
        String actualSgf=stringWriter.toString();
        if(expectedSgf==null) actualSgf=null; // hack for now
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testRoundTripeTwice() throws Exception {
        StringReader reader=expectedSgf!=null?new StringReader(expectedSgf):null;
        boolean isOk=sgfRoundTripTwice(reader);
        assertTrue(isOk);
    }
    @Ignore @Test public void testModelRT0() throws Exception {
        Model model=new Model();
        model.restore(new StringReader(expectedSgf));
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue(ok);
        String actualSgf=stringWriter.toString();
        System.out.println(actualSgf);
    }
    @Test public void testSaveAndRestore() throws Exception {
        // try to compare two trees for equality.
        // write a deep equals.
        //fail("nyi");
    }
    @Ignore @Test public void testModelRestoreAndSave() throws Exception {
        String actual=sgfRoundTrip(expectedSgf);
        assertEquals(key.toString(),expectedSgf,actual);
        // failing probably due to add new root problem
        Model model=new Model();
        System.out.println("ex: "+expectedSgf);
        model.restore(new StringReader(expectedSgf));
        StringWriter stringWriter=new StringWriter();
        boolean ok=model.save(stringWriter);
        assertTrue(ok);
        String actualSgf=stringWriter.toString();
        actualSgf=options.removeUnwanted(actualSgf);
        //Utilities.printDifferences(System.out,expectedSgf,actualSgf);
        System.out.println("ex: "+expectedSgf);
        System.out.println("ac0: "+actual);
        System.out.println("ac: "+actualSgf);
        //assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testHexAscii() {
        String encoded=expectedSgf!=null?HexAscii.encode(expectedSgf.getBytes()):null;
        String actualSgf=encoded!=null?HexAscii.decodeToString(encoded):null;
        assertEquals(expectedSgf,actualSgf);
    }
    @Test public void testCannonical() {
        String actualSgf=sgfRoundTrip(expectedSgf);
        String actual2=sgfRoundTrip(actualSgf);
        assertEquals(key.toString(),actualSgf,actual2);
    }
    @Ignore @Test public void testCheckBoardInRoot() {
        boolean ok=checkBoardInRoot(key);
        // always fails because none of these have a board in root.
        // that seems to be the usual case.
        // that is always the usual case,
        // since i always add a dummy multi-way node!
        // a better check would be if there was a board in the first real node.
        //assertTrue(ok);
    }
    Object key;
    String expectedSgf;
    SgfNode games;
}
