package sgf;
import static org.junit.Assert.assertNull;
import org.junit.Test;
public class ParserEdgeTestCase {
    @Test public void testNull() {
        SgfNode games=new Parser().parse((String)null);
        assertNull(games);
    }
    @Test public void testEmpty() {
        SgfNode games=new Parser().parse("");
        assertNull(games);
    }
}
