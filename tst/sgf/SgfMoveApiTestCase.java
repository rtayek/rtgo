package sgf;

import io.Logging;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import equipment.Board;
import equipment.Coordinates;
import equipment.Point;
import equipment.Stone;
import model.Model;
import model.Move2;
import model.MNodeTestIo;

public class SgfMoveApiTestCase {
    @Test public void testMoveAtA2() throws Exception {
        assertMove(Move2.whiteMoveAtA2);
        logMoves();
    }

    @Test public void testPass() throws Exception {
        assertMove(Move2.whitePass);
    }

    @Test public void testResign() throws Exception {
        assertMove(Move2.whiteResign);
    }

    @Test public void testSgfMakeMove() {
        Stone color=Stone.black;
        Point point=new Point(0,0);
        String expected=Coordinates.toGtpCoordinateSystem(point,sgfModel.board().width(),sgfModel.board().depth());
        assertEquals(Stone.vacant,sgfModel.board().at(point));
        assertEquals(0,sgfModel.moves());
        assertEquals(null,sgfModel.lastMoveGTP());
        sgfModel.sgfMakeMove(color,point);
        assertEquals(color,sgfModel.board().at(point));
        assertEquals(1,sgfModel.moves());
        assertEquals(expected,sgfModel.lastMoveGTP());
    }

    @Test public void testSgfUnmakeMove() {
        Point point=new Point(0,0);
        Stone expected=sgfModel.board().at(point);
        sgfModel.sgfMakeMove(Stone.black,point);
        sgfModel.sgfUnmakeMove(point);
        assertEquals(expected,sgfModel.board().at(point));
    }

    private void runBasicMove() {
        basicModel.move(Move2.blackMoveAtA1);
        moves=basicModel.moves();
        basicModel.move(expectedMove);
        String expectedSgf=MNodeTestIo.save(basicModel.root());
        actualMove=basicModel.lastMove2();
        String actualSgf2=SgfTestIo.mNodeRoundTrip(expectedSgf,SgfRoundTrip.MNodeSaveMode.standard);
        assertEquals(expectedSgf,actualSgf2);
    }

    private void assertMove(Move2 move) {
        expectedMove=move;
        runBasicMove();
        assertEquals(expectedMove,actualMove);
    }

    private void logMoves() {
        Logging.mainLogger.info("expected move: "+expectedMove);
        Logging.mainLogger.info("actual move: "+actualMove);
    }

    Model basicModel=new Model();
    {
        basicModel.ensureBoard();
    }
    Move2 expectedMove,actualMove;
    int moves;

    final Model sgfModel=new Model();
    {
        sgfModel.setBoard(Board.factory.create(Board.standard));
    }
}
