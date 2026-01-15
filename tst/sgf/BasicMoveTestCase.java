package sgf;
import io.Logging;
import model.Model;
import model.Move2;
import model.MNodeTestIo;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
public class BasicMoveTestCase {
    //fail("wait for move complete will hang!");
    //model.waitForMoveCompleteOnBoard(moves);
    // test all of these with gtp also!
    private void run() {
        model.move(Move2.blackMoveAtA1);
        moves=model.moves();
        model.move(expectedMove);
        // do a round trip. yes it is, but we have the move to make
        String expectedSgf=MNodeTestIo.save(model.root());
        actualMove=model.lastMove2();
        String actualSgf2=SgfTestIo.mNodeRoundTrip(expectedSgf,SgfRoundTrip.MNodeSaveMode.standard);
        assertEquals(expectedSgf,actualSgf2);
    }
    private void assertMove(Move2 move) {
        expectedMove=move;
        run();
        assertEquals(expectedMove,actualMove);
    }
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
    private void logMoves() {
        Logging.mainLogger.info("expected move: "+expectedMove);
        Logging.mainLogger.info("actual move: "+actualMove);
    }
    Model model=new Model();
    {
        model.ensureBoard();
    }
    int width=model.board().width(),depth=model.board().depth();
    Move2 expectedMove,actualMove;
    int moves;
}
