package equipment;
import static org.junit.Assert.*;
import java.util.List;
import org.junit.*;
import equipment.Board.*;
import io.Logging;
import model.Model;
import model.Model.MoveResult;
import utilities.*;
public class BlockTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    @Test public void testBlock() {
        Point point=new Point();
        board.setAt(0,0,Stone.black);
        Block block=new Block(board,point,new boolean[board.width()][board.depth()]);
        assertNotNull(block); // silly
    }
    @Test public void testColor() {
        board.setAt(0,0,Stone.black);
        Block block=new Block(board,0,0,new boolean[board.width()][board.depth()]);
        assertEquals(Stone.black,block.color());
    }
    @Test public void testPoints() {
        Point point=new Point();
        board.setAt(point,Stone.black);
        Block block=new Block(board,0,0,new boolean[board.width()][board.depth()]);
        List<Point> points=block.points();
        assertEquals(1,points.size());
        assertEquals(point,points.get(0));
    }
    @Test public void testToString() {
        Point point=new Point();
        board.setAt(point,Stone.black);
        Block block=new Block(board,point,new boolean[board.width()][board.depth()]);
        String actual=block.toString(); // just that we can call it i guess
        String expected="block: black 1 stone(s), 2 liberties[(0,0)]";
        assertEquals(expected,actual); // fragile!
    }
    @Test public void testGrow() {
        Point point=new Point();
        board.setAt(point,Stone.black);
        Point point2=new Point(0,1);
        board.setAt(point2,Stone.black);
        Block block=new Block(board,0,0,new boolean[board.width()][board.depth()]);
        assertEquals(2,block.points().size());
    }
    @Test public void testLibertiesx() {
        Point point=new Point(board.width()/2,board.depth()/2);
        board.setAt(point,Stone.black);
        Block block=new Block(board,point,new boolean[board.width()][board.depth()]);
        if(board.width()>=3&&board.depth()>=3) assertEquals(4,block.liberties());
    }
    @Test public void testLiberties() {
        board.setAt(0,0,Stone.black);
        Pair<List<Block>,List<Block>> pair=Block.findBlocks(board);
        assertEquals(0,pair.second.size());
        assertEquals(1,pair.first.size());
        assertEquals(2,pair.first.get(0).liberties()); // will fail sometimes
        board.setAt(0,0,Stone.vacant);
        board.setAt(1,1,Stone.black);
        pair=Block.findBlocks(board);
        assertEquals(0,pair.second.size());
        assertEquals(1,pair.first.size());
        assertEquals(4,pair.first.get(0).liberties());
    }
    @Test public void testFind() {
        Point point=new Point();
        board.setAt(point,Stone.black);
        Block block=Block.find(board,new boolean[board.width()][board.depth()],point.x,point.y);
        assertEquals(point,block.points().get(0));
    }
    @Test public void testFindBlock() {
        Point point=new Point();
        board.setAt(point,Stone.black);
        Block block=Block.find(board,point);
        assertEquals(point,block.points().get(0));
    }
    @Test public void testFindBlocks() {
        board.setAt(1,1,Stone.black);
        Pair<List<Block>,List<Block>> blocks=Block.findBlocks(board);
        assertEquals(1,blocks.first.size());
        assertEquals(0,blocks.second.size());
        board.setAt(1,2,Stone.white);
        blocks=Block.findBlocks(board);
        assertEquals(1,blocks.first.size());
        assertEquals(1,blocks.second.size());
    }
    @Test public void testFindBlocks2() {
        board.setAt(1,1,Stone.white);
        Pair<List<Block>,List<Block>> blocks=Block.findBlocks(board);
        assertEquals(0,blocks.first.size());
        assertEquals(1,blocks.second.size());
        board.setAt(1,2,Stone.black);
        blocks=Block.findBlocks(board);
        assertEquals(1,blocks.first.size());
        assertEquals(1,blocks.second.size());
    }
    @Test public void testFindCapturedStones() { Logging.mainLogger.warning("not implemented."); }
    @Test public void testLiberties2() {
        // this uesed to work. i broke it on 10/11/22
        Model model=new Model();
        { // why not just make a board?
            model.setRoot(9,9,Topology.normal,Shape.normal);
        }
        int width=model.board().width();
        // one place uses depth. find and fix.
        MoveResult moveResult=null;
        moveResult=model.move(Stone.black,"B9",width);
        model.move(Stone.white,"C9",width);
        model.move(Stone.black,"B8",width);
        model.move(Stone.white,"C8",width);
        model.move(Stone.black,"C7",width);
        model.move(Stone.white,"B7",width);
        model.move(Stone.black,"D8",width);
        model.move(Stone.white,"D7",width);
        model.move(Stone.black,"C6",width);
        model.move(Stone.white,"E8",width);
        model.move(Stone.black,"C5",width);
        model.move(Stone.white,"D9",width);
        Point point=Coordinates.fromGtpCoordinateSystem("C9",width);
        Block block=Block.find(model.board(),point);
        assertEquals(2,block.liberties());
    }

    Board board=Board.factory.create(5,Topology.normal);
}
