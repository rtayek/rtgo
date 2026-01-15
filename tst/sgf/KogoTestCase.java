package sgf;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.io.IOException;
import org.junit.*;
import utilities.MyTestWatcher;
@Ignore public class KogoTestCase extends AbstractIgnoreMoveAndSetupFlagsTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testKogo() throws IOException {
        boolean ok=SgfTestSupport.roundTripTwice(new File("sgf/KogosJosekiDictionary.sgf"));
        assertTrue(ok);
    }
}
