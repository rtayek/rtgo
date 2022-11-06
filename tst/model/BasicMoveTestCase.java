package model;
import static org.junit.Assert.assertEquals;
import java.io.*;
import org.junit.Test;
import equipment.Stone;
import sgf.MNode;
public class BasicMoveTestCase {
    //fail("wait for move complete will hang!");
    //model.waitForMoveCompleteOnBoard(moves);
    // test all of these with gtp also!
    void run() {
        //System.out.println("-------");
        model.move(Move.blackMoveAtA1);
        moves=model.moves();
        //System.out.println(expected+", sgf: "+expected.toSGFCoordinates(width,depth)+", gtp: "
        //        +expected.toGTPCoordinates(width,depth));
        model.move(expected);
        boolean ok=MNode.save(stringWriter,model.root(),null);
        if(!ok) throw new RuntimeException();
        String sgf=stringWriter.toString();
        System.out.println("s gf: "+sgf);
        actual=model.lastMove();
        lastMoveGtp=model.lastMoveGTP();
        lastColorGtp=model.lastColorGTP();
        MNode mNode=MNode.restore(new StringReader(sgf));
        StringWriter stringWriter2=new StringWriter();
        boolean ok2=MNode.save(stringWriter2,mNode,null);
        if(!ok2) throw new RuntimeException();
        String sgf2=stringWriter2.toString();
        System.out.println("sgf2: "+sgf2);
        assertEquals(sgf,sgf2);
    }
    @Test() public void testMoveAtA1() throws Exception {
        expected=Move.whiteMoveAtA2;
        run();
        assertEquals(expected,actual);
    }
    @Test() public void testPass() throws Exception {
        expected=Move.whitePass;
        run();
        assertEquals(expected,actual); }
    @Test public void testResign() throws Exception {
        expected=Move.whiteResign;
        run();
        assertEquals(expected,actual); }
    Model model=new Model();
    int width=model.board().width(),depth=model.board().depth();
    Move expected,actual;
    int moves;
    StringWriter stringWriter=new StringWriter();
    String lastMoveGtp;
    Stone lastColorGtp;
}
