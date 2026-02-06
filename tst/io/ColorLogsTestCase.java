package io;
import static org.junit.Assert.assertEquals;
import com.tayek.util.log.ColorLogs;
import org.junit.*;
import utilities.TestSupport;
public class ColorLogsTestCase extends TestSupport {
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void test() {
        for(String key:ColorLogs.map.keySet()) {
            String expected=ColorLogs.map.get(key);
            String actual=ColorLogs.escapeSequence(key);
            assertEquals(expected,actual);
            actual=ColorLogs.escapeSequence("foo"+key+"bar");
            assertEquals(expected,actual);
        }
    }
    @Test public void testLog() {
        for(String key:ColorLogs.map.keySet()) {
            Logging.mainLogger.info(String.valueOf(ColorLogs.color(key)));
            Logging.mainLogger.severe(ColorLogs.color(key));
        }
    }
}
