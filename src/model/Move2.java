package model;
import java.util.Objects;
import equipment.*;
import model.LegacyMove.MoveImpl;
// PASS & resign https://www.gnu.org/software/gnugo/gnugo_19.html
public final class Move2 {
    @Override public int hashCode() { return Objects.hash(color,moveType,name,point); }
    @Override public boolean equals(Object obj) {
        if(this==obj) return true;
        if(obj==null) return false;
        if(getClass()!=obj.getClass()) return false;
        Move2 other=(Move2)obj;
        return color==other.color&&moveType==other.moveType&&Objects.equals(name,other.name)
                &&Objects.equals(point,other.point);
    }
    public Move2(MoveType moveType,Stone color,Point point) { this(moveType,color,point,moveType.toString()); }
    public Move2(MoveType moveType,Stone color,Point point,String name) {
        this.name=name;
        this.moveType=moveType;
        this.color=color;
        this.point=point;
    }
    public enum MoveType { move, nullMove, pass, resign, } // setup or undo?
    public String name() { return name; }
    public final String name;
    public final MoveType moveType;
    public final Stone color; // or null if you really must
    public final Point point; // only for play
    public static Move2 move(Stone color,Point point,String name) {
        Objects.requireNonNull(color);
        Objects.requireNonNull(point);
        return new Move2(MoveType.move,color,point,name);
    }
    public static Move2 pass(Stone color) { return new Move2(MoveType.pass,color,null); }
    public static Move2 resign(Stone color) { return new Move2(MoveType.resign,color,null); }
    public String nameWithColor() { return color+" "+moveType; }
    public boolean isPass() { return moveType.equals(MoveType.pass); }
    public boolean isResign() { return moveType.equals(MoveType.resign); }
    public boolean isNull() { return moveType.equals(MoveType.nullMove); }
    boolean isMove() { return moveType==MoveType.move; }
    @Override public String toString() { /* keep whatever legacy format */ return moveType.toString(); }
    public static final Move2 nullMove=new Move2(MoveType.nullMove,Stone.vacant,null);
    public static final Move2 blackPass=new Move2(MoveType.pass,Stone.black,null);
    public static final Move2 whitePass=new Move2(MoveType.pass,Stone.white,null);
    public static final Move2 blackResign=new Move2(MoveType.resign,Stone.black,null);
    public static final Move2 whiteResign=new Move2(MoveType.resign,Stone.white,null);
    static final Move2 blackMoveAtA1=new Move2(MoveType.move,Stone.black,new Point());
    static final Move2 whiteMoveAtA2=new Move2(MoveType.move,Stone.white,new Point(0,1));
}
