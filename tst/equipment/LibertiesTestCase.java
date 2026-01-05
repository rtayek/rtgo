package equipment;
import static org.junit.Assert.*;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import equipment.Board.Topology;
import io.Logging;
import utilities.MyTestWatcher;
@RunWith(Parameterized.class) public class LibertiesTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    public LibertiesTestCase(Topology topology) { this.topology=topology; }
    @BeforeClass public static void setUpBeforeClass() throws Exception {}
    @AfterClass public static void tearDownAfterClass() throws Exception {}
    @Before public void setUp() throws Exception { board=Board.factory.create(3,topology); }
    @After public void tearDown() throws Exception {}
    @Parameters public static Collection<Object[]> data() {
        List<Object[]> list=new ArrayList<Object[]>();
        for(Board.Topology type:Board.Topology.values()) list.add(new Object[] {type});
        Logging.mainLogger.info(list.size()+" boards.");
        return list;
    }
    @Test public void testLibertiesForThisTopology() {
        Point c=new Point(board.width()/2,board.depth()/2);
        Point uL,uR,lL,lR;
        if(!topology.equals(Topology.diamond)) {
            lL=board.lL();
            lR=board.lR();
            uL=board.uL();
            uR=board.uR();
        } else { // rotate right 90 degrees for diamond shape
            // maybe do this in board constructor
            lL=new Point(0,board.depth()/2);
            lR=new Point(board.width()/2,0);
            uL=new Point(board.width()/2,board.depth()-1);
            uR=new Point(board.width()-1,board.depth()/2);
        }
        board.setAt(uL.x,uL.y,Stone.black);
        board.setAt(uR.x,uR.y,Stone.black);
        board.setAt(lL.x,lL.y,Stone.black);
        board.setAt(lR.x,lR.y,Stone.black);
        board.setAt(c.x,c.y,Stone.black);
        // use factory or constructor for block?
        Block center=new Block(board,c.x,c.y,new boolean[board.width()][board.depth()]);
        assertNotNull(center); // may no work some some shapes
        if(topology.equals(Topology.diamond)&&board.width()==3) {
            assertEquals(5,center.points().size());
            assertEquals(0,center.liberties());
        } else {
            assertEquals(1,center.points().size());
            assertEquals(4,center.liberties());
        }
        Block upperLeft=new Block(board,uL.x,uL.y,new boolean[board.width()][board.depth()]);
        Block upperRight=new Block(board,uR.x,uR.y,new boolean[board.width()][board.depth()]);
        Block lowerLeft=new Block(board,lL.x,lL.y,new boolean[board.width()][board.depth()]);
        Block lowerRight=new Block(board,lR.x,lR.y,new boolean[board.width()][board.depth()]);
        switch(board.topology()) {
            case normal:
                assertEquals(2,upperLeft.liberties());
                assertEquals(2,upperRight.liberties());
                assertEquals(2,lowerLeft.liberties());
                assertEquals(2,lowerRight.liberties());
                break;
            case horizontalCylinder:
                Logging.mainLogger.info(String.valueOf(board));
                Logging.mainLogger.info("uL: "+uL+" "+upperLeft);
                int expected;
                expected=board.width()==3?3:4;
                assertEquals(expected,upperLeft.liberties());
                assertEquals(expected,upperRight.liberties());
                assertEquals(expected,lowerLeft.liberties());
                assertEquals(expected,lowerRight.liberties());
                break;
            case verticalCylinder:
                expected=board.width()==3?3:4;
                assertEquals(expected,upperLeft.liberties());
                assertEquals(expected,upperRight.liberties());
                assertEquals(expected,lowerLeft.liberties());
                assertEquals(expected,lowerRight.liberties());
                break;
            case torus:
                expected=board.width()==3?4:8;
                assertEquals(expected,upperLeft.liberties());
                assertEquals(expected,upperRight.liberties());
                assertEquals(expected,lowerLeft.liberties());
                assertEquals(expected,lowerRight.liberties());
                break;
            case diamond:
                Logging.mainLogger.info(String.valueOf(board));
                expected=board.width()==3?0:1;
                assertEquals(expected,upperLeft.liberties());
                assertEquals(expected,upperRight.liberties());
                assertEquals(expected,lowerLeft.liberties());
                assertEquals(expected,lowerRight.liberties());
                break;
            default:
                throw new RuntimeException("oops: "+board.topology());
        }
    }
    Topology topology;
    Board board;
}
