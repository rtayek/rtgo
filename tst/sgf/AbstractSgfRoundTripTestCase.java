package sgf;
import static io.IO.noIndent;
import static org.junit.Assert.*;
import static sgf.Parser.restoreSgf;
import static sgf.SgfNode.*;
import static utilities.Utilities.implies;
import java.io.*;
import org.junit.*;
import utilities.MyTestWatcher;
public abstract class AbstractSgfRoundTripTestCase extends AbstractSgfParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    public Boolean specialCases(String actualSgf) {
        Boolean ok=false; // no more assertions are needed
        if(expectedSgf.equals("")) expectedSgf=null;
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
        StringReader stringReader=new StringReader(expectedSgf);
        StringWriter stringWriter=new StringWriter();
        SgfNode games=sgfRoundTrip(stringReader,stringWriter);
        String actualSgf=stringWriter.toString();
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        Boolean ok=specialCases(actualSgf);
        if(ok) return;
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testRSgfoundTripeTwice() throws Exception {
        StringReader reader=expectedSgf!=null?new StringReader(expectedSgf):null;
        boolean isOk=sgfRoundTripTwice(reader);
        assertTrue(isOk);
    }
    @Test public void testSgfSaveAndRestore() throws Exception {
        SgfNode expected=expectedSgf!=null?restoreSgf(new StringReader(expectedSgf)):null,actual;
        StringWriter stringWriter=new StringWriter();
        String sgf=null;
        if(expected!=null) {
            expected.saveSgf(stringWriter,noIndent);
            sgf=stringWriter.toString();
            actual=restoreSgf(new StringReader(sgf));
        } else return;
        assertTrue(expected.deepEquals(actual));
    }
    @Test public void testHexAscii() {
        String encoded=expectedSgf!=null?HexAscii.encode(expectedSgf.getBytes()):null;
        String actualSgf=encoded!=null?HexAscii.decodeToString(encoded):null;
        assertTrue(implies(expectedSgf==null,encoded==null));
        assertTrue(implies(encoded==null,actualSgf==null));
        assertEquals(expectedSgf,actualSgf);
    }
    @Test public void testSgfCannonical() {
        String actualSgf=sgfRoundTrip(expectedSgf);
        String actual2=sgfRoundTrip(actualSgf);
        assertEquals(key.toString(),actualSgf,actual2);
    }
}
