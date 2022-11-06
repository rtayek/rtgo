package equipment;
import static org.junit.Assert.*;
import java.util.List;
import org.junit.*;
import utilities.MyTestWatcher;
/*@FixMethodOrder(MethodSorters.NAME_ASCENDING)*/public abstract class BoardTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testWidth() {
        Board board2=Board.factory.create(Board.standard);
        assertEquals(Board.standard,(Integer)board2.width());
    }
    @Test public void testDepth() {
        Board board2=Board.factory.create(Board.standard);
        assertEquals(Board.standard,(Integer)board2.depth());
    }
    @Test public void testType() {
        assertNotNull(board.topology());
        // what are we testing here?
        // do we want to allow topology to change?
        // assertEquals(Board.Type.normal,board.type());
    }
    @Test public void testStones() {
        // logger.info(shortMethod());
        Stone[] stones=board.stones();
        assertEquals(board.width()*board.depth(),stones.length);
    }
    @Test public void testIsInRangePoint() { // not exhaustive
        // logger.info(shortMethod());
        assertFalse(board.isInRange(new Point(-1,-1)));
        assertTrue(board.isInRange(new Point(0,0)));
        // we could use the for corners here instead of the next 3
        assertTrue(board.isInRange(new Point(board.width()/2,board.depth()/2)));
        assertTrue(board.isInRange(new Point(board.width()-1,board.depth()-1)));
        assertFalse(board.isInRange(new Point(board.width(),board.depth())));
    }
    @Test public void testIsXInRange() { // not exhaustive
        // logger.info(shortMethod());
        assertFalse(board.isXInRange(-1));
        assertTrue(board.isXInRange(0));
        assertTrue(board.isXInRange(board.width()/2));
        assertTrue(board.isXInRange(board.width()-1));
        assertFalse(board.isXInRange(board.width()));
    }
    @Test public void testIsYInRange() { // not exhaustive
        // logger.info(shortMethod());
        assertFalse(board.isYInRange(-1));
        assertTrue(board.isYInRange(0));
        assertTrue(board.isYInRange(board.depth()/2));
        assertTrue(board.isYInRange(board.depth()-1));
        assertFalse(board.isYInRange(board.depth()));
    }
    @Test public void testIsInRangeIntInt() { // not exhaustive
        // logger.info(shortMethod());
        assertFalse(board.isInRange(-1,-1));
        assertTrue(board.isInRange(0,0));
        assertTrue(board.isInRange(board.width()/2,board.depth()/2));
        assertTrue(board.isInRange(board.width()-1,board.depth()-1));
        assertFalse(board.isInRange(board.width(),board.depth()));
    }
    @Test public void testIsOnBoardPoint() { // not exhaustive
        // logger.info(shortMethod());
        assertFalse(board.isOnBoard(new Point(-1,-1)));
        // again we could use the 4 points from the board
        assertTrue(board.isOnBoard(new Point(0,0)));
        assertTrue(board.isOnBoard(new Point(board.width()/2,board.depth()/2)));
        assertTrue(board.isOnBoard(new Point(board.width()-1,board.depth()-1)));
        assertFalse(board.isOnBoard(new Point(board.width(),board.depth())));
        board.setAt(new Point(board.width()/2,board.depth()/2),Stone.edge); // hole
        // above could step on some stone already there?
        assertFalse(board.isOnBoard(new Point(board.width()/2,board.depth()/2)));
    }
    @Test public void testIsOnBoardIntInt() { // not exhaustive
        // logger.info(shortMethod());
        assertFalse(board.isOnBoard(-1,-1));
        assertTrue(board.isOnBoard(0,0));
        assertTrue(board.isOnBoard(board.width()/2,board.depth()/2));
        assertTrue(board.isOnBoard(board.width()-1,board.depth()-1));
        assertFalse(board.isOnBoard(board.width(),board.depth()));
        board.setAt(board.width()/2,board.depth()/2,Stone.edge); // hole
        // above could step on some stone already there?
        assertFalse(board.isOnBoard(board.width()/2,board.depth()/2));
    }
    @Test public void testIndexIntInt() {
        // logger.info(shortMethod());
        for(int x=0;x<board.width();x++) for(int y=0;y<board.depth();y++) {
            int index=board.index(x,y);
            assertEquals(board.width()*y+x,index);
        }
    }
    @Test public void testIndexPoint() {
        // logger.info(shortMethod());
        for(int x=0;x<board.width();x++) for(int y=0;y<board.depth();y++) {
            int index=board.index(new Point(x,y));
            assertEquals(board.index(x,y),index);
        }
    }
    private void setAndCheckIntForSome() { setAndCheck(0); setAndCheck(1); setAndCheck(board.width()*board.depth()-1); }
    @Test public void testAtInt() {
        // logger.info(shortMethod());
        setAndCheckIntForSome();
    }
    private void setAndCheck(int index) {
        Stone color=board.at(index);
        if(!color.equals(Stone.edge)) if(color.equals(Stone.vacant)) {
            board.setAt(index,Stone.black);
            assertTrue(board.at(index).equals(Stone.black));
        }
    }
    private void setAndCheck(int x,int y) {
        Stone color=board.at(x,y);
        if(!color.equals(Stone.edge)) if(color.equals(Stone.vacant)) {
            board.setAt(x,y,Stone.black);
            assertTrue(board.at(x,y).equals(Stone.black));
        }
    }
    private void setAndCheck(Point point) {
        Stone color=board.at(point);
        if(!color.equals(Stone.edge)) if(color.equals(Stone.vacant)) {
            board.setAt(point,Stone.black);
            assertTrue(board.at(point).equals(Stone.black));
        }
    }
    @Test public void testAtIntInt() {
        // logger.info(shortMethod());
        int x=0,y=0;
        setAndCheck(x,y);
    }
    @Test public void testAtPoint() {
        // logger.info(shortMethod());
        Point point=new Point();
        setAndCheck(point);
    }
    @Test public void testSetAtPointStone() {
        // logger.info(shortMethod());
        Point point=new Point();
        setAndCheck(point);
    }
    @Test public void testSetAtIntIntStone() {
        // logger.info(shortMethod());
        int x=0,y=0;
        setAndCheck(x,y);
    }
    @Test public void testSetAtIntStone() {
        // logger.info(shortMethod());
        setAndCheckIntForSome();
    }
    @Test public void testSetAll() {
        // logger.info(shortMethod());
        for(int x=0;x<board.width();x++) for(int y=0;y<board.depth();y++) setAndCheck(x,y);
    }
    @Test public void testIsEqual() {
        // logger.info(shortMethod());
        Board board2=Board.factory.create(board.width(),board.depth(),board.topology());
        for(int x=0;x<board.width();x++) for(int y=0;y<board.depth();y++) board2.setAt(x,y,board.at(x,y));
        assertTrue(board2.isEqual(board));
    }
    @Test public void testStarPoints() {
        if(board.hasStarPoints()) {
            List<Point> starPoints=board.starPoints();
            assertNotNull(starPoints);
            for(Point point:starPoints) assertTrue(board.isOnBoard(point)); // may fail if there's a
            // hole here!
        }
    }
    public Board board;
}
