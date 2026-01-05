package sgf;
import static org.junit.Assert.assertNull;
import java.io.Reader;
import org.junit.Test;
public class ParserEdgeTestCase {
    @Test public void testNull() { SgfNode games=SgfTestIo.restore((Reader)null); assertNull(games); }
    @Test public void testEmpty() { SgfNode games=SgfTestIo.restore(""); assertNull(games); }
}
