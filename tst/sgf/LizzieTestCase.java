package sgf;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
public class LizzieTestCase extends AbstractWatchedTestCase {
    @Test public void testLizzie() { String id="LZ"; P p=P.idToP.get(id); assertNotNull(p); }
}
