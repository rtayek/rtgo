package equipment;
import static org.junit.Assert.*;
import java.util.List;
import org.junit.*;
import equipment.Board.Shape;
import utilities.MyTestWatcher;
/*@FixMethodOrder(MethodSorters.NAME_ASCENDING)*/ public class BoardImplTestCase extends BoardABCTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    //public BoardImplTestCase(BoardImpl boardImpl) {
    //	this.boardImpl=boardImpl;
    //}
    @Override @Before public void setUp() throws Exception {
        super.setUp();
        board=boardImpl=new BoardImpl(Board.standard,Board.standard,Board.Topology.normal,Shape.normal,0);
    }
    @Override @After public void tearDown() throws Exception {}
    @Test public void txestBoardImplInt() {
        for(Board.Topology type:Board.Topology.values()) {
            Board board2=new BoardImpl(board.width(),board.depth(),type,Shape.normal,0);
            assertNotNull(board2);
        }
    }
    @Test public void testBoardImplIntIntType() {
        for(Board.Topology type:Board.Topology.values()) {
            Board board2=Board.factory.create(board.width(),board.depth(),type);
            assertNotNull(board2); // just testing the factory
        }
    }
    @Test public void testStonesImpl() { // duplicated in board abc test case
        Stone[] stones=board.stones();
        assertEquals(board.width()*board.depth(),stones.length);
    }
    @Test public void testSetAtImpl() {
        int width=5,depth=3;
        BoardImpl boardImpl=new BoardImpl(width,depth,Board.Topology.normal,Shape.normal,0);
        for(int k=0;k<width*depth;k++) {
            boardImpl.setAt(k,Stone.black);
            assertEquals(Stone.black,boardImpl.at(k));
        }
    }
    @Test public void testStarPointsImpl() {
        // logger.info(shortMethod());
        if(boardImpl.hasStarPoints()) {
            List<Point> starPoints=boardImpl.starPoints();
            assertNotNull(starPoints);
            for(Point point:starPoints)
                if(!boardImpl.at(point).equals(Stone.edge)) assertTrue(boardImpl.isOnBoard(point));
            // what are we testing here?
        }
    }
    @Test public void testRandomImpl() {
        for(int n=Board.smallest;n<=Board.largest;n++) assertNotNull(BoardImpl.random(n));
    }
    @Test public void testCopy() {
        Point point=new Point(0,0);
        Stone expected=Stone.black;
        boardImpl=new BoardImpl(9,0);
        boardImpl.setAt(point,expected);
        Board board=boardImpl.copy();
        assertEquals(expected,board.at(point));
        assertTrue(boardImpl.isEqual(board));
    }
    BoardImpl boardImpl;
}
