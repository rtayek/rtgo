package sgf;
import static org.junit.Assert.*;
import static sgf.SgfNode.*;
import java.io.StringReader;
import org.junit.*;
import utilities.MyTestWatcher;
public abstract class AbstractSgfRoundTripTestCase extends AbstractParserTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testRoundTrip() throws Exception {
        String actualSgf=sgfRoundTrip(expectedSgf);
        if(actualSgf!=null) actualSgf=SgfNode.options.prepareSgf(actualSgf);
        if(expectedSgf==null) {
            if(actualSgf!=null) fail("expected is null. actual is not null");
        } else if(!expectedSgf.equals(actualSgf)); //printDifferences(expected,actual);
        assertEquals(key.toString(),expectedSgf,actualSgf);
    }
    @Test public void testRoundTripeTwice() throws Exception {
        StringReader reader=expectedSgf!=null?new StringReader(expectedSgf):null;
        boolean isOk=sgfRoundTripTwice(reader);
        assertTrue(isOk);
    }
    @Test public void testSaveAndRestore() throws Exception {
        // do a restore, then round trip?
        // try to compare two trees for equality.
        // maybe we already have that.
        // write a deep equals.
        //fail("nyi");
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
