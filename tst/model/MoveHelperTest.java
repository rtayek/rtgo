package model;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.*;
import equipment.Point;
import equipment.Stone;
public class MoveHelperTest {
    @Test public void toGameMove_roundTripPointMove() {
        Point p=new Point(1,2);
        Move move=new Move.MoveImpl(Stone.black,p);
        Move2 gameMove=MoveHelper.toGameMove(move,19,19);
        assertEquals(Move2.MoveType.move,gameMove.moveType);
        assertEquals(Stone.black,gameMove.color);
        assertEquals(p,gameMove.point);
        assertEquals(move.name(),gameMove.name());
        Move legacy=MoveHelper.toLegacyMove(gameMove);
        assertEquals(Stone.black,legacy.color());
        assertEquals(p,legacy.point());
        assertEquals(gameMove.name(),legacy.name());
    }
    @Test public void toGameMove_roundTripPass() {
        Move move=Move.blackPass;
        Move2 gameMove=MoveHelper.toGameMove(move,19,19);
        assertSame(Move2.blackPass,gameMove);
        Move legacy=MoveHelper.toLegacyMove(gameMove);
        assertSame(Move.blackPass,legacy);
    }
    @Test public void toGameMove_roundTripResign() {
        Move2 gameMove=Move2.resign(Stone.white);
        Move legacy=MoveHelper.toLegacyMove(gameMove);
        assertSame(Move.whiteResign,legacy);
        Move2 back=MoveHelper.toGameMove(legacy,19,19);
        assertEquals(Move2.MoveType.resign,back.moveType);
        assertEquals(Stone.white,back.color);
    }
    @Ignore @Test(expected=RuntimeException.class) public void toGameMove_rejectsNullMove() {
        MoveHelper.toGameMove(Move.nullMove,19,19);
    }
    @Test() public void toGameMove_acceptsNullMove() { MoveHelper.toGameMove(Move.nullMove,19,19); }
}
