package model;
import java.util.*;
import controller.*;
import controller.Command;
import equipment.*;
import static model.MoveHelper.*;
// maybe have a type of move that is setup or ?
public interface LegacyMove {
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
    public abstract class MoveABC implements LegacyMove {
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
            return Coordinates.toGtpCoordinateSystem(point,width,depth); // uses to use Board.standard width/depth 
        }
        // above is a problem. we don't know width or depth :)
        /// write a test for this!
        @Override public String toString() { return color()+" "+name()+" "+point; }
    }
    public static class NoMove extends MoveABC {
        // test to see if we can use this as root for game forest.
        // does not seem to be used anywhere.
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
            return color().equals(Stone.black)?LegacyMove.blackPass.name():LegacyMove.whitePass.name();
        }
        @Override public String toString() { return name(); }
    }
    public static class Resign extends MoveABC {
        Resign(Stone color) { super(color,gtpResignString); }
        @Override public boolean isResign() { return true; }
        @Override public String toSGFCoordinates(int width,int depth) { return gtpResignString; } // is this a bug?
        @Override public String toGTPCoordinates(int width,int depth) {
            return color().equals(Stone.black)?LegacyMove.blackResign.name():LegacyMove.whiteResign.name();
        }
        @Override public String toString() { return name(); }
    }
    public static class Null extends MoveABC {
        Null() { super(Stone.vacant,"nullMove"); }
        @Override public String toSGFCoordinates(int width,int depth) { return ""; }
        @Override public String toGTPCoordinates(int width,int depth) { return LegacyMove.nullMove.name(); }
        @Override public String toString() { return name(); }
    }
    // PASS & resign https://www.gnu.org/software/gnugo/gnugo_19.html
}