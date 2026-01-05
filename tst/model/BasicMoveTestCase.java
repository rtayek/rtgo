package model;
import io.Logging;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import sgf.SgfRoundTrip;
import sgf.SgfTestIo;
public class BasicMoveTestCase {
    //fail("wait for move complete will hang!");
    //model.waitForMoveCompleteOnBoard(moves);
    // test all of these with gtp also!
    void run() {
        model.move(Move2.blackMoveAtA1);
        moves=model.moves();
        model.move(expectedMove);
        // do a round trip. yes it is, but we have the move to make
        String expectedSgf=MNodeTestIo.save(model.root());
        actualMove=model.lastMove2();
        String actualSgf2=SgfTestIo.mNodeRoundTrip(expectedSgf,SgfRoundTrip.MNodeSaveMode.standard);
        assertEquals(expectedSgf,actualSgf2);
    }
    @Test() public void testMoveAtA2() throws Exception {
        expectedMove=Move2.whiteMoveAtA2;
        run();
        Logging.mainLogger.info("expected move: "+expectedMove);
        Logging.mainLogger.info("actual move: "+actualMove);
        assertEquals(expectedMove,actualMove);
    }
    @Test() public void testPass() throws Exception {
        expectedMove=Move2.whitePass;
        run();
        assertEquals(expectedMove,actualMove);
    }
    @Test public void testResign() throws Exception {
        expectedMove=Move2.whiteResign;
        run();
        assertEquals(expectedMove,actualMove);
    }
    Model model=new Model();
    {
        model.ensureBoard();
    }
    int width=model.board().width(),depth=model.board().depth();
    Move2 expectedMove,actualMove;
    int moves;
}
