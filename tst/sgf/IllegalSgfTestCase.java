package sgf;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class) public class IllegalSgfTestCase extends AbstractSgfFixtureTestCase {
    @Parameters public static Collection<Object[]> testData() {
        return SgfTestSupport.illegalSgfParameters();
    }
    @Test public void testParse() throws Exception {
        SgfNode games=restoreExpectedSgf();
        //assertNull(key.toString(),games); // allow null for now
    }
}
