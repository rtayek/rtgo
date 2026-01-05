package equipment;
import io.Logging;
import java.util.List;
import org.junit.*;
import equipment.Board.*;
public class DiamondTestCase {
    @BeforeClass public static void setUpBeforeClass() throws Exception {}
    @AfterClass public static void tearDownAfterClass() throws Exception {}
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void test() {
        List<Point> points=Board.getPointsForDiamondRegion(board.width(),board.depth());
        for(Point point:points) board.setAt(point,Stone.edge);
        Logging.mainLogger.info(String.valueOf(board));
        Logging.mainLogger.info("----------------");
        Logging.mainLogger.info(String.valueOf(points));
    }
    int n=9;
    Board board=Board.factory.create(n,n,Topology.diamond,Shape.normal);
}
