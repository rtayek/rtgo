package equipment;
import static org.junit.Assert.assertEquals;
import org.junit.*;
import utilities.TestSupport;
public class PointTestCase extends TestSupport {
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
