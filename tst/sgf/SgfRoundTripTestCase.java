package sgf;
import static org.junit.Assert.assertEquals;
import static sgf.SgfNode.sgfRoundTrip;
import org.junit.*;
import utilities.MyTestWatcher;
public abstract class SgfRoundTripTestCase {
    // maybe integrate with abstract sg test case
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testSgfRoundTrip() throws Exception {
        expected=expected.replace("\n","");
        String actual=sgfRoundTrip(expected);
        actual=actual.replace("\n",""); // who is putting the linefeed in?
        if(!expected.equals(actual)); //printDifferences(expected,actual);
        assertEquals(expected,actual);
    }
    public String expected;
}
