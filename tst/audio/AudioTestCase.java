package audio;
import org.junit.Rule;
import utilities.MyTestWatcher;
import static org.junit.Assert.assertNotNull;
import java.io.InputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
public class AudioTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testGetResourceAsStream() {
        InputStream x=Audio.getResourceAsStream("gochlng.wav");
        assertNotNull(x);
    }
}

