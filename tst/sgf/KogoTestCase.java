package sgf;
import static org.junit.Assert.assertTrue;
import static sgf.SgfNode.sgfRoundTripTwice;
import java.io.*;
import org.junit.*;
import io.IOs;
import utilities.MyTestWatcher;
@Ignore public class KogoTestCase {
    @Before public void setUp() throws Exception {
        old=SgfNode.ignoreMoveAndSetupFlags;
        SgfNode.ignoreMoveAndSetupFlags=true;
    }
    @After public void tearDown() throws Exception { SgfNode.ignoreMoveAndSetupFlags=old; }
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testKogo() throws IOException {
        Reader reader=IOs.toReader(new File("sgf/KogosJosekiDictionary.sgf"));
        boolean ok=sgfRoundTripTwice(reader);
        assertTrue(ok);
    }
    boolean old;
}
