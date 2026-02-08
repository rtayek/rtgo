package equipment;
import utilities.MyTestWatcher;
import io.Logging;
import static org.junit.Assert.*;
import org.junit.*;
import equipment.Board.*;
public class BoardFactoryTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testCreateInt() {
        for(int n=Board.smallest;n<Board.largest;n++) {
            board=Board.factory.create(n);
            assertNotNull(board);
        }
    }
    @Test public void testCreateIntType() {
        for(int n=Board.smallest;n<Board.largest;n++) {
            board=Board.factory.create(n,Topology.normal);
            assertNotNull(board);
        }
    }
    @Test public void testCreateIntIntType() {
        for(int n=Board.smallest;n<Board.largest;n++) {
            board=Board.factory.create(n,n,Topology.normal);
            assertNotNull(board);
        }
    }
    @Test public void testCreateIntIntType2() {
        int failures=0;
        for(Topology topology:Topology.values()) {
            board=Board.factory.create(19,19,topology);
            if(board==null) { ++failures; Logging.mainLogger.info(topology+" fails!"); }
            //assertNotNull(board);
        }
        if(failures>0) Logging.mainLogger.info(failures+" failures.");
        assertEquals(0,failures);
    }
    @Test public void testCreateIntIntTypeShape() {
        int failures=0;
        for(Topology topology:Topology.values()) for(Shape shape:Shape.values()) {
            board=Board.factory.create(19,19,topology,shape);
            if(topology.equals(Topology.diamond)&&shape.equals(Shape.programmer)); //
            else if(board==null) { ++failures; Logging.mainLogger.info(topology+" "+shape+" fails!"); }
            //assertNotNull(board);
        }
        if(failures>0) Logging.mainLogger.info(failures+" failures.");
        assertEquals(0,failures);
    }
    Board board;
}

