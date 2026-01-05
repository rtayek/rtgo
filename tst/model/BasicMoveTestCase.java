package model;
import static org.junit.Assert.assertEquals;
import java.io.*;
import org.junit.Test;
import sgf.SgfRoundTrip;
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
        StringWriter stringWriter=new StringWriter();
        SgfRoundTrip.mNodeRoundTrip(new StringReader(expectedSgf),stringWriter,SgfRoundTrip.MNodeSaveMode.standard);
        String actualSgf2=stringWriter.toString();
        assertEquals(expectedSgf,actualSgf2);
    }
    @Test() public void testMoveAtA2() throws Exception {
        expectedMove=Move2.whiteMoveAtA2;
        run();
        System.out.println("expected move: "+expectedMove);
        System.out.println("actual move: "+actualMove);
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
