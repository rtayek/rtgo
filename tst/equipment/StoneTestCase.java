package equipment;
import static org.junit.Assert.assertEquals;
import org.junit.*;
import org.junit.rules.TestRule;
import io.Logging;
import utilities.MyTestWatcher;
public class StoneTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testToCharacter() {
        assertEquals(Character.valueOf('x'),Stone.black.toCharacter());
        assertEquals(Character.valueOf('o'),Stone.white.toCharacter());
    }
    @Test public void testOtherColor() {
        Logging.mainLogger.info("in test other color.");
        assertEquals(Stone.black,Stone.white.otherColor());
        assertEquals(Stone.white,Stone.black.otherColor());
    }
    @Test public void testToString() { for(Stone stone:Stone.values()) assertEquals(stone.name(),stone.toString()); }
}
