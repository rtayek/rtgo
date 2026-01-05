package model;
import static org.junit.Assert.assertEquals;
import java.io.*;
import org.junit.Test;
import sgf.MNode;
public class BasicMoveTestCase {
    //fail("wait for move complete will hang!");
    //model.waitForMoveCompleteOnBoard(moves);
    // test all of these with gtp also!
    void run() {
        model.move(Move2.blackMoveAtA1);
        moves=model.moves();
        model.move(expectedMove);
        boolean ok=MNode.save(stringWriter,model.root(),null);
        if(!ok) throw new RuntimeException();
        // do a round trip. yes it is, but we have the move to make
        String expectedSgf=stringWriter.toString();
        actualMove=model.lastMove2();
        MNode mNode=MNode.restore(new StringReader(expectedSgf));
        StringWriter stringWriter2=new StringWriter();
        // instead of new, maybe: https://stackoverflow.com/questions/3738095/how-do-you-empty-a-stringwriter-in-java
        boolean ok2=MNode.save(stringWriter2,mNode,null);
        if(!ok2) throw new RuntimeException();
        String actualSgf2=stringWriter2.toString();
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
    StringWriter stringWriter=new StringWriter();
}
