package model;
import static org.junit.Assert.*;
import static sgf.Parser.getSgfData;
import java.io.*;
import org.junit.*;
import equipment.*;
import model.Model.MoveResult;
import model.Move.MoveImpl;
import sgf.*;
import utilities.MyTestWatcher;
public class IllegalMoveTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testResignOutOfOrder() {
        model.move(new MoveImpl(Stone.black,new Point()));
        model.move(Move.blackPass);
    }
    @Test public void testA1A2x() throws IOException {
        model.move(Stone.black,"A1",model.board().width());
        assertTrue(model.checkParity());
        StringWriter stringWriter=new StringWriter();
        boolean ok=MNode.save(stringWriter,model.currentNode(),null);
        // what does using current node here mean?
        assertTrue(ok);
        model.move(Stone.black,"A2",model.board().width());
        assertTrue(model.checkParity());
        stringWriter=new StringWriter();
        ok=MNode.save(stringWriter,model.currentNode(),null);
        assertTrue(ok);
        model.up();
        assertTrue(model.checkParity());
    }
    @Test public void testIsLegalMoveOnOccupoedPoint() {
        //model.setRoot();
        Point a1=new Point(0,0);
        Move move=new MoveImpl(model.turn(),a1);
        MoveResult actual=model.isLegalMove(move);
        assertEquals(MoveResult.legal,actual);
        model.move(move);
        Move whiteMove=new MoveImpl(model.turn().otherColor(),a1);
        actual=model.isLegalMove(whiteMove);
        assertEquals(MoveResult.occupied,actual);
        actual=model.isLegalMove(move);
        assertEquals(MoveResult.occupied,actual);
    }
    @Test public void testBlackA1WhiteA1() {
        // lower level test than testIsLegalMoveOnOccupoedPoint()
        // maybe that is where out of turn stuff is handled?
        //model.setRoot();
        Point a1=new Point(0,0);
        Move move=new MoveImpl(model.turn(),a1);
        MoveResult ok=model.addMoveNodeAndExecute(move);
        assertEquals(MoveResult.legal,ok);
        ok=model.addMoveNodeAndExecute(move);
        assertEquals(MoveResult.occupied,ok);
    }
    @Test public void testIsLegalMoveOnOccupiedPoint2() {
        // currently it is illegal to move out of turn
        // but sgf and gtp seem to handle it fine.
        // how is this happening?
        // maybe gto changes the color somehow?
        // model.setRoot();
        Point a1=new Point();
        // maybe just do the add move node and execute?
        Move move=new MoveImpl(model.turn(),a1);
        MoveResult ok=model.isLegalMove(move);
        assertEquals(MoveResult.legal,ok);
        ok=model.addMoveNodeAndExecute(move);
        assertEquals(MoveResult.legal,ok);
        ok=model.isLegalMove(move);
        assertEquals(MoveResult.occupied,ok);
        ok=model.addMoveNodeAndExecute(move);
        assertEquals(MoveResult.occupied,ok);
        move=new MoveImpl(model.turn().otherColor(),a1);
        ok=model.isLegalMove(move);
        assertEquals(MoveResult.occupied,ok);
        move=new MoveImpl(model.turn().otherColor(),a1);
        ok=model.addMoveNodeAndExecute(move);
        assertEquals(MoveResult.occupied,ok);
    }
    Model model=new Model();
    final String expectedSGF=getSgfData(Parser.consecutiveMoves);
}
