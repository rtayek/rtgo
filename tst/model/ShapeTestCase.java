package model;
import static org.junit.Assert.*;
import java.util.List;
import org.junit.*;
import equipment.*;
import equipment.Board.*;
import io.Logging;
import utilities.MyTestWatcher;
public class ShapeTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void test1() throws Exception {
        List<Point> points=Board.squares(1,19,19);
        Logging.mainLogger.warning(String.valueOf(points.toString()));
        points=Board.squares(2,19,19);
        Logging.mainLogger.warning(String.valueOf(points.toString()));
        //what do we test here?
    }
    @Test public void testShapes() throws Exception {
        int width=19,depth=19;
        Topology topology=Topology.normal;
        for(Shape shape:Shape.values()) {
            Model model=new Model();
            model.setBoardTopology(Topology.normal);
            model.setBoardShape(shape);
            model.setRoot(width,depth,topology,shape);
            model.ensureBoard();
            // triange?
            Board board=model.board();
            List<Point> points=topology==Topology.diamond?points=Board.getPointsForDiamondRegion(width,depth)
                    :Shape.getPointsForRegion(width,depth,shape);
            for(Point point:points) assertEquals(shape+" "+point,Stone.edge,board.at(point));
        }
    }
}
