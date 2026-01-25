package audio;
import static org.junit.Assert.assertNotNull;
import java.io.InputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utilities.TestSupport;
public class AudioTestCase extends TestSupport {
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testGetResourceAsStream() {
        InputStream x=Audio.getResourceAsStream("gochlng.wav");
        assertNotNull(x);
    }
}
