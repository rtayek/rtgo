package model;
import java.util.*;
import controller.*;
import controller.Command;
import equipment.*;
import static model.MoveHelper.*;
// maybe have a type of move that is setup or ?
interface Move_ {}
public interface Move extends Move_ {
    public final class Move__ {
        public enum MoveTYpe { move, pass, resign, } // setup or undo?
        public final MoveTYpe moveType;
        public final Stone color; // or null if you really must
        public final Point point; // only for play
        public static Move__ move(Stone color,Point point) {
            Objects.requireNonNull(color);
            Objects.requireNonNull(point);
            return new Move__(MoveTYpe.move,color,point);
        }
        public static Move__ pass(Stone color) { return new Move__(MoveTYpe.pass,color,null); }
        public static Move__ resign(Stone color) { return new Move__(MoveTYpe.resign,color,null); }
        public String nameWithColor() { return color+" "+moveType; }
        public boolean isPass() { return moveType.equals(MoveTYpe.pass); }
        public boolean isResign() { return moveType.equals(MoveTYpe.resign); }
        private Move__(MoveTYpe moveType,Stone color,Point point) {
            this.moveType=moveType;
            this.color=color;
            this.point=point;
        }
        boolean isMove() { return moveType==MoveTYpe.move; }

        @Override public String toString() { /* keep whatever legacy format */ return moveType.toString(); }
        public static final Move__ blackPass=new Move__(MoveTYpe.pass,Stone.black,null); 
        Move__ whitePass=new Move__(MoveTYpe.pass,Stone.white,null);
        Move__ blackResign=new Move__(MoveTYpe.resign,Stone.white,null);
        Move__ whiteResign=new Move__(MoveTYpe.resign,Stone.black,null);
    }
    String name();
    Stone color(); // we may need this
    default String nameWithColor() { return color()+" "+name(); }
    default boolean isPass() { return false; }
    default boolean isResign() { return false; }
    String toSGFCoordinates(int width,int depth); // unused?
    String toGTPCoordinates(int width,int depth);
    default Point point() { return null; }
    Pass blackPass=new Pass(Stone.black);
    Pass whitePass=new Pass(Stone.white);
    Resign blackResign=new Resign(Stone.black);
    Resign whiteResign=new Resign(Stone.white);
    Null nullMove=new Null();
    public abstract class MoveABC implements Move {
        @Override public Stone color() { return color; }
        @Override public int hashCode() { return point()!=null?Objects.hash(point()):Objects.hash(name); }
        @Override public boolean equals(Object obj) {
            if(this==obj) return true;
            if(obj==null) return false;
            if(!(obj instanceof MoveABC)) return false;
            MoveABC other=(MoveABC)obj;
            return Objects.equals(name(),other.name())&&Objects.equals(point(),other.point())
                    &&Objects.equals(color(),other.color());
        }
        MoveABC(Stone color,String name) { this.color=color; this.name=name; }
        @Override public String name() { return name; }
        // maybe we need a gtpName?
        @Override public String toString() { return name; }
        private final String name;
        private final Stone color;
    }
    public static class MoveImpl extends MoveABC {
        public MoveImpl(Stone color,Point point) { super(color,"move"); this.point=point; }
        final Point point;
        @Override public Point point() { return point; }
        @Override public String toSGFCoordinates(int width,int depth) {
            return Coordinates.toSgfCoordinates(point,/*width,*/depth);
            // looks like sgf uses depth
            // i use width everywhere else
        }
        @Override public String toGTPCoordinates(int width,int depth) {
            return Coordinates.toGtpCoordinateSystem(point,Board.standard,Board.standard);
        }
        // above is a problem. we don't know width or depth :)
        /// write a test for this!
        @Override public String toString() { return color()+" "+name()+" "+point; }
    }
    public static class NoMove extends MoveABC {
        // test to see if we can use this as root for game forest.
        private NoMove() { super(null,null); }
        @Override public String toSGFCoordinates(int width,int depth) { // TODO Auto-generated method stub
            return null;
        }
        @Override public String toGTPCoordinates(int width,int depth) { // TODO Auto-generated method stub
            return null;
        }
    }
    public static class Pass extends MoveABC {
        private Pass(Stone color) { super(color,gtpPassString); }
        @Override public boolean isPass() { return true; }
        @Override public String toSGFCoordinates(int width,int depth) {
            return gtpPassString;
            // was returning "" - check for this!
        }
        @Override public String toGTPCoordinates(int width,int depth) {
            return color().equals(Stone.black)?Move.blackPass.name():Move.whitePass.name();
        }
        @Override public String toString() { return name(); }
    }
    public static class Resign extends MoveABC {
        Resign(Stone color) { super(color,gtpResignString); }
        @Override public boolean isResign() { return true; }
        @Override public String toSGFCoordinates(int width,int depth) { return gtpResignString; } // is this a bug?
        @Override public String toGTPCoordinates(int width,int depth) {
            return color().equals(Stone.black)?Move.blackResign.name():Move.whiteResign.name();
        }
        @Override public String toString() { return name(); }
    }
    public static class Null extends MoveABC {
        Null() { super(Stone.vacant,"nullMove"); }
        @Override public String toSGFCoordinates(int width,int depth) { return ""; }
        @Override public String toGTPCoordinates(int width,int depth) { return Move.nullMove.name(); }
        @Override public String toString() { return name(); }
    }
    static final Move blackMoveAtA1=new MoveImpl(Stone.black,new Point());
    static final Move whiteMoveAtA2=new MoveImpl(Stone.white,new Point(0,1));
    // PASS & resign https://www.gnu.org/software/gnugo/gnugo_19.html
}