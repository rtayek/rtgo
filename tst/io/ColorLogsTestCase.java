package io;
import static org.junit.Assert.assertEquals;
import org.junit.*;
import utilities.MyTestWatcher;
public class ColorLogsTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
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
            System.out.println(ColorLogs.color(key));
            Logging.mainLogger.severe(ColorLogs.color(key));
        }
    }
}
