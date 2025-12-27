package model;
import java.util.Objects;
import equipment.*;
public final class Move2 {
    public enum MoveTYpe { move, nullMove, pass, resign, } // setup or undo?
    public final MoveTYpe moveType;
    public final Stone color; // or null if you really must
    public final Point point; // only for play
    public static Move2 move(Stone color,Point point) {
        Objects.requireNonNull(color);
        Objects.requireNonNull(point);
        return new Move2(MoveTYpe.move,color,point);
    }
    public static Move2 pass(Stone color) { return new Move2(MoveTYpe.pass,color,null); }
    public static Move2 resign(Stone color) { return new Move2(MoveTYpe.resign,color,null); }
    public String nameWithColor() { return color+" "+moveType; }
    public boolean isPass() { return moveType.equals(MoveTYpe.pass); }
    public boolean isResign() { return moveType.equals(MoveTYpe.resign); }
    public boolean isNull() { return moveType.equals(MoveTYpe.nullMove); }
    private Move2(MoveTYpe moveType,Stone color,Point point) {
        this.moveType=moveType;
        this.color=color;
        this.point=point;
    }
    boolean isMove() { return moveType==MoveTYpe.move; }
    @Override public String toString() { /* keep whatever legacy format */ return moveType.toString(); }
    public static final Move2 nullMove=new Move2(MoveTYpe.nullMove,Stone.vacant,null);
    public static final Move2 blackPass=new Move2(MoveTYpe.pass,Stone.black,null);
    public static final Move2 whitePass=new Move2(MoveTYpe.pass,Stone.white,null);
    public static final Move2 blackResign=new Move2(MoveTYpe.resign,Stone.black,null);
    public static final Move2 whiteResign=new Move2(MoveTYpe.resign,Stone.white,null);
}
