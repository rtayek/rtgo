package sgf;
import static org.junit.Assert.assertNull;
import static sgf.Parser.restoreSgf;
import org.junit.Test;
public class ParserEdgeTestCase {
    @Test public void testNull() {
        SgfNode games=restoreSgf((String)null);
        assertNull(games);
    }
    @Test public void testEmpty() {
        SgfNode games=restoreSgf("");
        assertNull(games);
    }
}
