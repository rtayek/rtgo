package equipment;
import static org.junit.Assert.*;
import java.util.List;
import org.junit.*;
import equipment.Board.*;
import io.Logging;
import utilities.MyTestWatcher;
/*@FixMethodOrder(MethodSorters.NAME_ASCENDING)*/public abstract class BoardABCTestCase extends BoardTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    // this test case may not need to be run for a bunch of strange boards?
    @Test public void testBoardABC() { assertNotNull(boardABC); }
    @Test public void testTypeABC() { assertEquals(topology,boardABC.topology); }
    @Test public void testWidthABC() { assertEquals(width,boardABC.width); }
    @Test public void testDepthABC() { assertEquals(depth,boardABC.depth); }
    @Test public void testNeighbors() {
        // logger.info(shortMethod());
        Point point=new Point();
        List<Point> actual=Neighborhood.neighbor4s(point);
        // can't test boards with holes without a lot of trouble.
        // maybe fragile. maybe make the list
        String expected="[(-1,0), (1,0), (0,-1), (0,1)]";
        assertEquals(expected,actual.toString());
    }
    @Test public void testNeighborsOfCorners() {
        List<Point> actual=Neighborhood.neighbor8s(0,0);
        // can't test boards with holes without a lot of trouble.
        // maybe fragile. maybe make the list
        String expected="[(-1,0), (1,0), (0,-1), (0,1), (0,0), (-1,-1), (1,-1), (-1,1), (1,1)]";
        //System.out.println(expected);
        //System.out.println(actual);
        // extremely fragile, make a of points
        assertEquals(expected,actual.toString());
    }
    @Test public void testToStringImpl() {
        int width=6,depth=3;
        {
            BoardImpl boardImpl=new BoardImpl(width,depth,Topology.normal,Shape.normal,0);
            boardImpl.setAt(0,Stone.black);
            boardImpl.setAt(width/2,depth/2,Stone.edge);
            boardImpl.setAt(width-1,depth-1,Stone.white);
            String string=boardImpl.toString();
            Logging.mainLogger.info(string);
        }
    }
    int width=3,depth=5;
    Topology topology=Topology.normal;
    Shape shape=Shape.normal;
    BoardABC boardABC=new BoardABC(width,depth,topology,shape,0) {
        @Override public Stone[] stones() { return null; }
        @Override public List<Point> starPoints() { return null; }
        @Override public void setAt(int k,Stone color) {}
    };
}
