package model;
import static org.junit.Assert.assertEquals;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import equipment.Board;
import equipment.Coordinates;
import equipment.Point;
import equipment.Stone;
import utilities.MyTestWatcher;
public class SgfMakeMoveTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Before public void setUp() throws Exception {}
    @After public void tearDown() throws Exception {}
    // maybe add pass and resign?
    @Test public void testSgfMakMovee() {
        Stone color=Stone.black;
        Point point=new Point(0,0);
        String expected=Coordinates.toGtpCoordinateSystem(point,model.board().width(),model.board().depth());
        assertEquals(Stone.vacant,model.board().at(point));
        assertEquals(0,model.moves());
        assertEquals(null,model.lastMoveGTP());
        model.sgfMakeMove(color,point);
        assertEquals(color,model.board().at(point));
        assertEquals(1,model.moves());
        assertEquals(expected,model.lastMoveGTP());
    }
    @Test public void testSgfUnmakeMove() {
        Point point=new Point(0,0);
        Stone expected=model.board().at(point);
        model.sgfMakeMove(Stone.black,point);
        model.sgfUnmakeMove(point);
        assertEquals(expected,model.board().at(point));
    }
    final Model model=new Model();
    {
        model.setBoard(Board.factory.create(Board.standard));
    }
}
