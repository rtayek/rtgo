package audio;
import static org.junit.Assert.assertNotNull;
import java.io.InputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import utilities.MyTestWatcher;
public class AudioTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testGetResourceAsStream() { InputStream x=Audio.getResourceAsStream("gochlng.wav"); assertNotNull(x); }
}
