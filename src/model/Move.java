package model;
import java.util.*;
import controller.*;
import controller.Command;
import equipment.*;
import io.IO.End.Holder;
import io.Logging;
// maybe have a type of move that is setup or ?
public interface Move {
    String name();
    Stone color(); // we may need this
    default String nameWithColor() { return color()+" "+name(); }
    default boolean isPass() { return false; }
    default boolean isResign() { return false; }
    String toSGFCoordinates(int width,int depth); // unused?
    String toGTPCoordinates(int width,int depth);
    // maybe change default the above to methods for pass and resign?
    default Point point() { return null; }
    static Pass blackPass=new Pass(Stone.black);
    static Pass whitePass=new Pass(Stone.white);
    static Resign blackResign=new Resign(Stone.black);
    static Resign whiteResign=new Resign(Stone.white);
    static Null nullMove=new Null();
    static Move fromGTP(Stone color,String string,int width,int depth) {
        if(string==null) return Move.nullMove;
        else if(string.equals("")) {
            System.out.println("string is: "+"\"\"");
            return Move.nullMove; // pass?
        } else if(string.contains(gtpPassString)) {
            if(color.equals(Stone.black)) return Move.blackPass;
            else if(color.equals(Stone.white)) return Move.whitePass;
            else throw new RuntimeException("pass bad color!");
        } else if(string.contains(gtpResignString)) {
            if(color.equals(Stone.black)) return Move.blackResign;
            else if(color.equals(Stone.white)) return Move.whiteResign;
            else throw new RuntimeException("resign bad color!");
        }
        Point point=Coordinates.fromGtpCoordinateSystem(string,width);
        // check to see if point is on board? or at lease in range?
        return new MoveImpl(color,point);
    }
    static List<String> toGTPMoves(List<Move> moves,int width,int depth) {
        List<String> commands=new ArrayList<>();
        for(Move move:moves) { // how about pass and resign?
            String string=Command.play.name()+" "+move.color()+" "+move.toGTPCoordinates(width,depth);
            commands.add(string);
        }
        return commands;
    }
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
        @Override public String toSGFCoordinates(int width,int depth) { return gtpResignString; }
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
    public static Model pushGTPMovesToCurrentStateDirect(Model original,boolean oneAtATime) {
        Model model=new Model();
        if(original.board()!=null) { // normally no access to both of these at the same time
            model.setRoot(original.board().width(),original.board().depth());
            // probably need to set other stuff like shape etc.
        }
        // should set the board shape and topology also?
        List<String> gtpMoves=original.gtpMovesToCurrentState();
        boolean ok=GTPBackEnd.checkMoveCommandsDirect(model,gtpMoves,oneAtATime);
        if(!ok) Logging.mainLogger.severe("push fails on: "+gtpMoves);
        return model;
    }
    // should we do this before sending commands?
    //int width=model.board().width();
    //int depth=model.board().depth();
    // use width and depth command?
    //String command=Command.boardsize.name()+" "+width+":"+depth;
    //gtpMoves.add(0,command);
    static void getMovesAndPush(GTPFrontEnd frontEnd,Model model,boolean oneAtATime) {
        List<String> gtpMoves=model.gtpMovesToCurrentState();
        if(oneAtATime) {
            for(String gtpMove:gtpMoves) {
                Response response=frontEnd.sendAndReceive(gtpMove);
                if(!response.isOk()) Logging.mainLogger.severe(response+" is not ok!");
            }
        } else frontEnd.sendAndReceive(gtpMoves);
    }
    public static Model pushGTPMovesToCurrentStateBoth(Model original,boolean oneAtATime) {
        Model model=new Model("model");
        if(original.board()!=null) { // normally no access to both of these at the same time
            model.setRoot(original.board().width(),original.board().depth());
            // probably need to set other stuff like shape etc.
        }
        BothEnds both=new BothEnds();
        Holder holder=Holder.duplex();
        both.setupBoth(holder,"test",model);
        @SuppressWarnings("unused") Thread back=both.backEnd.startGTP(0);
        getMovesAndPush(both.frontEnd,original,oneAtATime);
        return model;
    }
    public static void main(String[] args) {
        Model original=new Model();
        original.setRoot(5,5);
        original.move(Stone.black,new Point());
        original.move(Stone.white,new Point(1,1));
        Model model=Move.pushGTPMovesToCurrentStateDirect(original,false);
        if(!model.board().isEqual(original.board())) System.out.println("fail!");
        Model model2=Move.pushGTPMovesToCurrentStateDirect(original,true);
        if(!model2.board().isEqual(original.board())) System.out.println("fail!");
        Model model3=Move.pushGTPMovesToCurrentStateBoth(original,true);
        if(!model3.board().isEqual(original.board())) System.out.println("fail!");
        Model model4=Move.pushGTPMovesToCurrentStateBoth(original,false);
        if(!model4.board().isEqual(original.board())) System.out.println("fail!");
    }
    static final Move blackMoveAtA1=new MoveImpl(Stone.black,new Point());
    static final Move whiteMoveAtA2=new MoveImpl(Stone.white,new Point(0,1));
    // PASS & resign https://www.gnu.org/software/gnugo/gnugo_19.html
    static String gtpPassString="pass",gtpResignString="resign";
}