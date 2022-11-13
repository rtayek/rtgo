package sgf;
import static io.IO.noIndent;
import static org.junit.Assert.*;
import static sgf.Parser.restoreSgf;
import static sgf.SgfNode.*;
import java.io.*;
import org.junit.*;
import utilities.MyTestWatcher;
public abstract class AbstractSgfRoundTripTestCase extends AbstractParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testRoundTrip() throws Exception {
        System.out.println("ex after fix: "+expectedSgf);
        String actualSgf=sgfRoundTrip(expectedSgf);
        System.out.println("---------------------");
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        if(expectedSgf==null) {
            if(actualSgf!=null) fail("expected is null. actual is not null");
        } else if(expectedSgf.equals("")) {
            if(actualSgf!=null) fail("expected is \"\". actual is not null");
        } else if(!expectedSgf.equals(actualSgf)); //printDifferences(expected,actual);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testRoundTripeTwice() throws Exception {
        StringReader reader=expectedSgf!=null?new StringReader(expectedSgf):null;
        boolean isOk=sgfRoundTripTwice(reader);
        assertTrue(isOk);
    }
    @Test public void testSaveAndRestore() throws Exception {
        SgfNode expected=restoreSgf(new StringReader(expectedSgf)),actual;
        StringWriter stringWriter=new StringWriter();
        String sgf=null;
        if(expected!=null) {
            expected.saveSgf(stringWriter,noIndent);
            sgf=stringWriter.toString();
            actual=restoreSgf(new StringReader(sgf));
        } else return;
        System.out.println("ex: "+expected);
        System.out.println("sgf: "+sgf);
        System.out.println("ac: "+actual);
        assertTrue(expected.deepEquals(actual));
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
}
