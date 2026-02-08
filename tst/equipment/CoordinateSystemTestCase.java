package equipment;
import utilities.MyTestWatcher;
import static org.junit.Assert.assertEquals;
import java.awt.geom.Point2D;
import org.junit.*;
import io.Logging;
import model.Model;
public class CoordinateSystemTestCase {
    @Rule public final MyTestWatcher watcher = new MyTestWatcher(getClass());
    @Test public void CheckStandard() {
        Point point=new Point();
        String string=Coordinates.toGtpCoordinateSystem(point,board.width(),board.depth());
        assertEquals("A1",string);
    }
    @Test public void coordinateToSgfCoordinateSystem() { // with i!
        for(int x=0;x<board.width();x++) if(board.depth()==board.width()) for(int y=0;y<board.depth();y++) {
            Point expected=new Point(x,y);
            String string=Coordinates.toSgfCoordinates(expected,board.depth());
            Point actual=Coordinates.fromSgfCoordinates(string,board.depth());
            assertEquals(expected,actual);
        }
        else Logging.mainLogger.info("test not done!");
    }
    @Test public void SgFCoordinateToCoordinateSystem() { // with i!
        for(int x=0;x<board.width();x++) if(board.depth()==board.width()) for(int y=0;y<board.depth();y++) {
            Point point=new Point(x,y);
            String expected=Coordinates.toSgfCoordinates(point,board.depth());
            Point point2=Coordinates.fromSgfCoordinates(expected,board.depth());
            String actual=Coordinates.toSgfCoordinates(point2,board.depth());
            assertEquals(expected,actual);
        }
        else Logging.mainLogger.info("test not done!");
    }
    @Test public void coordinateToStandardCoordinateSystem() { // no i!
        for(int x=0;x<board.width();x++) if(board.depth()==board.width()) for(int y=0;y<board.depth();y++) {
            Point expected=new Point(x,y);
            String string=Coordinates.toGtpCoordinateSystem(expected,board.width(),board.depth());
            Point actual=Coordinates.fromGtpCoordinateSystem(string,board.width());
            assertEquals(expected,actual);
        }
        else Logging.mainLogger.info("test not done!");
    }
    @Test public void StandardCoordinateSystemToCoordinateSystem() { // no i!
        for(int x=0;x<board.width();x++) if(board.depth()==board.width()) for(int y=0;y<board.depth();y++) {
            Point point=new Point(x,y);
            String expected=Coordinates.toGtpCoordinateSystem(point,board.width(),board.depth());
            Point point2=Coordinates.fromGtpCoordinateSystem(expected,board.width());
            String actual=Coordinates.toGtpCoordinateSystem(point2,board.width(),board.depth());
            assertEquals(expected,actual);
        }
        else Logging.mainLogger.info("test not done!");
    }
    @Test public void testSize() {
        for(int n=Board.smallest;n<=Board.largest;n++) {
            Board board=new BoardImpl(n,0);
            assertEquals(n,board.width());
            assertEquals(n,board.depth());
            Point point=new Point(board.width(),board.depth());
            Logging.mainLogger.info(String.valueOf(point));
                    
        }
    }
    @Test public void testAt() {
        for(int x=0;x<board.width();x++) for(int y=0;y<board.depth();y++) assertEquals(Stone.vacant,board.at(x,y));
    }
    @Test public void testSetAt() {
        for(Stone stone:Stone.values()) for(int x=0;x<board.width();x++) for(int y=0;y<board.width();y++) {
            board.setAt(x,y,stone);
            assertEquals(stone,board.at(x,y));
        }
    }
    @Test public void testA19aa018() { // A19 aa (0,18)
        Point expected=new Point(0,18);
        Point actual=Coordinates.fromSgfCoordinates("aa",board.depth());
        assertEquals(expected,actual);
        String noI=Coordinates.toGtpCoordinateSystem(expected,board.width(),board.depth());
        assertEquals("A19",noI);
    }
    @Test public void test_A1_as_00_19x19() { // A1 as (0,0)
        Point expected=new Point(0,0);
        Point actual=Coordinates.fromSgfCoordinates("as",Board.standard);
        assertEquals(expected,actual);
        String noI=Coordinates.toGtpCoordinateSystem(actual,Board.standard,Board.standard);
        assertEquals("A1",noI);
    }
    @Test public void test_A1_ai_00_9x9() { // A1 ai (0,0) 9x9
        Point expected=new Point(0,0);
        Point actual=Coordinates.fromSgfCoordinates("ai",9);
        assertEquals(expected,actual);
        String noI=Coordinates.toGtpCoordinateSystem(actual,9,9);
        assertEquals("A1",noI);
    }
    @Test public void test_R16_qd_1615_19x19() { // qd (16,15) R16
        Point expected=new Point(16,15);
        Point actual=Coordinates.fromSgfCoordinates("qd",Board.standard);
        assertEquals(expected,actual);
        String noI=Coordinates.toGtpCoordinateSystem(actual,Board.standard,Board.standard);
        assertEquals("R16",noI);
    }
    @Test public void test_G6_gd_65_9x9() { // gd (6,3) G6
        Point expected=new Point(6,5);
        Point actual=Coordinates.fromSgfCoordinates("gd",9);
        assertEquals(expected,actual);
        String noI=Coordinates.toGtpCoordinateSystem(actual,9,9);
        assertEquals("G6",noI);
    }
    @Test public void ToSeeWhereScreenCoordunatesAre2() {
        Point p0=new Point(0,0),dp=new Point(10,10);
        Point expected=p0;
        Logging.mainLogger.info("my point: "+p0);
        Point screen=Coordinates.toScreenCoordinates(expected,p0,dp,Board.standard);
        Logging.mainLogger.info("screen: "+screen);
        Point2D.Float actual=Coordinates.toBoardCoordinates(screen,p0,dp,Board.standard);
        assertEquals(expected,actual);
        expected=new Point(18,18);
        Logging.mainLogger.info("my point: "+expected);
        screen=Coordinates.toScreenCoordinates(expected,p0,dp,Board.standard);
        Logging.mainLogger.info("screen: "+screen);
        actual=Coordinates.toBoardCoordinates(screen,p0,dp,Board.standard);
        assertEquals(expected,actual);
    }
    @Test public void testBigBoard() {
        int n=Model.LargestBoardSize;
        Board board=new BoardImpl(n,0);
        for(int i=0;i<n;++i)
            for(int j=0;j<n;++j) {
                Point expected=new Point(i,j);
                String string=Coordinates.toSgfCoordinates(expected,board.depth());
                Point actual=Coordinates.fromSgfCoordinates(string,board.depth());
                assertEquals(expected,actual);
            }
    }
    Board board=new BoardImpl(Board.standard,0);
    static double epsilon=1e-9;
}

