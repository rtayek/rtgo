package equipment;
import utilities.MyTestWatcher;
import static org.junit.Assert.assertEquals;
import org.junit.*;
public class PointTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    @Test public void testPoint() { Point point=new Point(); assertEquals(0,point.x); assertEquals(0,point.y); }
    @Test public void testPointIntInt() {
        Point point=new Point(1,2);
        assertEquals(1,point.x);
        assertEquals(2,point.y);
    }
    @Test public void testPointPoint() {
        Point expected=new Point(1,2);
        Point actual=new Point(expected);
        assertEquals(expected.x,actual.x);
        assertEquals(expected.y,actual.y);
    }
    @Test public void testToString() { Point point=new Point(1,2); assertEquals("(1,2)",point.toString()); }
}

