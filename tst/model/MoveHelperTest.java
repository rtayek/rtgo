package model;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import org.junit.*;
import equipment.Point;
import equipment.Stone;
public class MoveHelperTest {
    @Test public void toGameMove_roundTripPointMove() {
        Point p=new Point(1,2);
        LegacyMove move=new LegacyMove.MoveImpl(Stone.black,p);
        Move2 gameMove=LegacyMoveHelper.toGameMove(move,19,19);
        assertEquals(Move2.MoveType.move,gameMove.moveType);
        assertEquals(Stone.black,gameMove.color);
        assertEquals(p,gameMove.point);
        assertEquals(move.name(),gameMove.name());
        LegacyMove legacy=LegacyMoveHelper.toLegacyMove(gameMove);
        assertEquals(Stone.black,legacy.color());
        assertEquals(p,legacy.point());
        assertEquals(gameMove.name(),legacy.name());
    }
    @Test public void toGameMove_roundTripPass() {
        LegacyMove move=LegacyMove.blackPass;
        Move2 gameMove=LegacyMoveHelper.toGameMove(move,19,19);
        assertSame(Move2.blackPass,gameMove);
        LegacyMove legacy=LegacyMoveHelper.toLegacyMove(gameMove);
        assertSame(LegacyMove.blackPass,legacy);
    }
    @Test public void toGameMove_roundTripResign() {
        Move2 gameMove=Move2.resign(Stone.white);
        LegacyMove legacy=LegacyMoveHelper.toLegacyMove(gameMove);
        assertSame(LegacyMove.whiteResign,legacy);
        Move2 back=LegacyMoveHelper.toGameMove(legacy,19,19);
        assertEquals(Move2.MoveType.resign,back.moveType);
        assertEquals(Stone.white,back.color);
    }
    @Test public void toGameMove_acceptsNullMove() {
        Move2 m=LegacyMoveHelper.toGameMove(LegacyMove.nullMove,19,19);
        assertSame(Move2.nullMove,m);
    }
}
