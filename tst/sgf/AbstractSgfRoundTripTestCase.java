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
        //System.out.println("ex after fix: "+expectedSgf);
        //String actualSgf=sgfRoundTrip(expectedSgf);
        StringWriter stringWriter=new StringWriter();
        SgfNode games=sgfRoundTrip(new StringReader(expectedSgf),stringWriter);
        String actualSgf=stringWriter.toString();
        if(expectedSgf.equals("")) expectedSgf=null;
        else if(expectedSgf.equals("()")) expectedSgf=null;
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        if(expectedSgf==null) {
            if(actualSgf!=null) fail("expected is null. actual is not null");
            return;
        } else {
            if(!expectedSgf.equals(actualSgf)) {
                System.out.println("ac: "+actualSgf);
                System.out.println(games);
                System.out.println(games.left+" "+games.right);
                System.out.println(games.left.left+" "+games.left.right);
                SgfNode x=restoreSgf(new StringReader(actualSgf));
                setIsAMoveFlags(x);
            }
        }
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testRoundTripeTwice() throws Exception {
        StringReader reader=expectedSgf!=null?new StringReader(expectedSgf):null;
        boolean isOk=sgfRoundTripTwice(reader);
        assertTrue(isOk);
    }
    @Test public void testSaveAndRestore() throws Exception {
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
    public static boolean implies(Boolean a,boolean b) { return !a|b; }
    @Test public void testHexAscii() {
        String encoded=expectedSgf!=null?HexAscii.encode(expectedSgf.getBytes()):null;
        String actualSgf=encoded!=null?HexAscii.decodeToString(encoded):null;
        assertTrue(implies(expectedSgf==null,encoded==null));
        assertTrue(implies(encoded==null,actualSgf==null));
        assertEquals(expectedSgf,actualSgf);
    }
    @Test public void testCannonical() {
        String actualSgf=sgfRoundTrip(expectedSgf);
        String actual2=sgfRoundTrip(actualSgf);
        assertEquals(key.toString(),actualSgf,actual2);
    }
}
