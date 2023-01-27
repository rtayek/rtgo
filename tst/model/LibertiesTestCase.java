package model;
import static org.junit.Assert.*;
import org.junit.Test;
import equipment.*;
import equipment.Board.*;
import model.Model.MoveResult;
public class LibertiesTestCase {
    @Test public void testLiberties2() {
        // this uesed to work. i broke it on 10/11/22
        Model model=new Model();
        Board board=Board.factory.create(9,9,Topology.normal,Shape.normal);
        model.setBoard(board);
        System.out.println(
        model.state().widthFromSgf+" "+
        model.state().depthFromSgf);

        // we need to actually do the sgf for the above
        System.out.println(model);
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
        System.out.println(model);
        Point point=Coordinates.fromGtpCoordinateSystem("C9",width);
        Block block=Block.find(model.board(),point);
        assertEquals(2,block.liberties());
    }
}
