package sgf;
import static org.junit.Assert.*;
import static sgf.Parser.restoreSgf;
import static sgf.SgfNode.*;
import java.io.*;
import org.junit.*;
import utilities.MyTestWatcher;
public abstract class AbstractSgfRoundTripTestCase extends AbstractSgfParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    // add setup and check alwaysPrepare
    //         if(alwaysPrepare) prepare();
    @Override @Before public void setUp() throws Exception { super.setUp(); if(!alwaysPrepare) prepare(); }
    private Boolean specialCases(String actualSgf) {
        Boolean ok=false; // no more assertions are needed
        if(expectedSgf.equals("")) expectedSgf=null; //11/29/22
        else if(expectedSgf.equals("()")) expectedSgf=null;
        if(expectedSgf==null) {
            if(actualSgf==null) ok=true;
            else if(actualSgf.equals("")) { actualSgf=null; ok=true; }
        } else {
            if(!expectedSgf.equals(actualSgf)) {
                if(expectedSgf.contains("PC[OGS: https://online-go.com/")) {
                    System.out.println("passing ogs file");
                    ok=true;
                }
            }
        }
        return ok;
    }
    @Test public void testSgfRoundTrip() throws Exception {
        if(expectedSgf==null) return;
        StringReader stringReader=new StringReader(expectedSgf);
        StringWriter stringWriter=new StringWriter();
        SgfNode games=sgfRoundTrip(stringReader,stringWriter);
        if(games!=null&&games.right!=null) System.out.println("42 more than one game!");
        String actualSgf=stringWriter.toString();
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        // how to do this more often?
        //Boolean ok=specialCases(actualSgf);
        //if(ok) return;
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testRSgfoundTripeTwice() throws Exception {
        assertFalse(expectedSgf.contains("\n"));
        StringReader reader=expectedSgf!=null?new StringReader(expectedSgf):null;
        boolean isOk=sgfRoundTripTwice(reader);
        assertTrue(key.toString(),isOk);
    }
    @Test public void testSgfSaveAndRestore() throws Exception {
        assertFalse(expectedSgf.contains("\n"));
        SgfNode expected=expectedSgf!=null?restoreSgf(new StringReader(expectedSgf)):null;
        StringWriter stringWriter=new StringWriter();
        SgfNode actualSgf=roundTrip(expected,stringWriter);
        if(expected!=null) assertTrue(key.toString(),expected.deepEquals(actualSgf));
    }
    @Test public void testSgfCannonical() {
        assertFalse(expectedSgf.contains("\n"));
        String actualSgf=sgfRoundTrip(expectedSgf);
        String actual2=sgfRoundTrip(actualSgf);
        assertEquals(key.toString(),actualSgf,actual2);
    }
}
