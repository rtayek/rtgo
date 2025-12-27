package core.api;


import java.util.Objects;
import equipment.Point;
import equipment.Stone;

public final class Move__ {
    public enum MoveTYpe { move, pass, resign,} // setup or undo?

    final MoveTYpe moveType;
    final Stone color;     // or null if you really must
    final Point point;     // only for play

    static Move__ move(Stone color, Point point) {
        Objects.requireNonNull(color);
        Objects.requireNonNull(point);
        return new Move__(MoveTYpe.move, color, point);
    }

    static Move__ pass(Stone color)   { return new Move__(MoveTYpe.pass, color, null); }
    static Move__ resign(Stone color) { return new Move__(MoveTYpe.resign, color, null); }

    private Move__(MoveTYpe moveType, Stone color, Point point) {
        this.moveType = moveType;
        this.color = color;
        this.point = point;
    }

    boolean isMove() { return moveType == MoveTYpe.move; }

    @Override public String toString() { /* keep whatever legacy format */ return moveType.toString(); }
}
