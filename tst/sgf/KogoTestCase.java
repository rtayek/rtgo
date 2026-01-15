package sgf;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import org.junit.Ignore;
import org.junit.Test;
@Ignore public class KogoTestCase extends AbstractIgnoreMoveAndSetupFlagsTestCase {
    @Test public void testKogo() throws IOException {
        boolean ok=SgfTestSupport.roundTripTwice(new File("sgf/KogosJosekiDictionary.sgf"));
        assertTrue(ok);
    }
}
