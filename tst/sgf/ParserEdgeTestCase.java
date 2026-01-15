package sgf;
import static org.junit.Assert.assertNull;
import java.io.Reader;
import org.junit.Test;
public class ParserEdgeTestCase {
    private static void assertRestoresNull(SgfNode games) {
        assertNull(games);
    }
    @Test public void testNull() { assertRestoresNull(SgfTestIo.restore((Reader)null)); }
    @Test public void testEmpty() { assertRestoresNull(SgfTestIo.restore("")); }
}
