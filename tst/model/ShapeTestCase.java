package model;
import java.util.List;
import org.junit.*;
import equipment.*;
import io.Logging;
import utilities.MyTestWatcher;
public class ShapeTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void test1() throws Exception {
        List<Point> points=Board.squares(1,19,19);
        Logging.mainLogger.warning(points.toString());
        points=Board.squares(2,19,19);
        Logging.mainLogger.warning(points.toString());
        //what do we test here?
    }
}
