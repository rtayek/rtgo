package model;
import java.util.*;
import controller.*;
import controller.Command;
import equipment.*;
import io.IOs.End.Holder;
import io.Logging;
import model.LegacyMove.*;
import model.Move2.MoveType;
public class LegacyMoveHelper {
    static Move2 toGameMove(LegacyMove legacy,int width,int depth) {
        //@Override public String toSGFCoordinates(int width,int depth) { return ""; }
        //@Override public String toGTPCoordinates(int width,int depth) { return Move.nullMove.name(); }
        if(legacy instanceof Pass) {
            if(legacy.color().equals(Stone.black)) return Move2.blackPass;
            else if(legacy.color().equals(Stone.white)) return Move2.whitePass;
            else throw new RuntimeException("pass bad color!");
        } else if(legacy instanceof Resign) {
            if(legacy.color().equals(Stone.black)) return Move2.blackResign;
            else if(legacy.color().equals(Stone.white)) return Move2.whiteResign;
            else throw new RuntimeException("resign bad color!");
        } else if(legacy instanceof Null) {
            return Move2.nullMove;
        } else if(legacy instanceof MoveImpl) {
            Point point=legacy.point();
            return Move2.move(legacy.color(),point,legacy.name());
        } else throw new RuntimeException("unknown legacy move type: "+legacy);
    }
    static LegacyMove toLegacyMove(Move2 m) {
        if(m.isMove()) {
            return new MoveImpl(m.color,m.point);
        } else if(m.isPass()) {
            if(m.color.equals(Stone.black)) return LegacyMove.blackPass;
            else if(m.color.equals(Stone.white)) return LegacyMove.whitePass;
            else throw new RuntimeException("pass bad color!");
        } else if(m.isResign()) {
            if(m.color.equals(Stone.black)) return LegacyMove.blackResign;
            else if(m.color.equals(Stone.white)) return LegacyMove.whiteResign;
            else throw new RuntimeException("resign bad color!");
        } else if(m.isNull()) {
            return LegacyMove.nullMove;
        } else throw new RuntimeException("unknown Move2 type: "+m);
    }
    private static String toSGFCoordinates(LegacyMove move,int width,int depth) {
        if(move instanceof Pass) {
            return gtpPassString; // was returning "" - check for this!
            // looks like a bug!
        } else if(move instanceof Resign) {
            return gtpResignString;
        } else if(move instanceof Null) {
            throw new RuntimeException("null move has no sgf coordinates!");
        } else if(move instanceof MoveImpl) {
            // looks like sgf uses depth
            // i use width everywhere else
            return Coordinates.toSgfCoordinates(move.point(),/*width,*/depth);
        } else throw new RuntimeException("unknown move type for sgf coordinates: "+move);
    }
    private static String toGTPCoordinates(LegacyMove move,int width,int depth) {
        if(move instanceof Pass) {
            return move.color().equals(Stone.black)?LegacyMove.blackPass.name():LegacyMove.whitePass.name();
        } else if(move instanceof Resign) {
            return move.color().equals(Stone.black)?LegacyMove.blackResign.name():LegacyMove.whiteResign.name();
        } else if(move instanceof Null) {
            return ""; // treat null as no-op for encoding
        } else if(move instanceof MoveImpl) {
            return Coordinates.toGtpCoordinateSystem(move.point(),width,depth);
        } else throw new RuntimeException("unknown move type for gtp coordinates: "+move);
    }
    public static String toGTPCoordinates(Move2 move,int width,int depth) {
        if(move.isPass()) {
            return move.color.equals(Stone.black)?Move2.blackPass.name():Move2.whitePass.name();
        } else if(move.isResign()) {
            return move.color.equals(Stone.black)?Move2.blackResign.name():Move2.whiteResign.name();
        } else if(move.isNull()) {
            return "";
        } else if(move.isMove()) {
            return Coordinates.toGtpCoordinateSystem(move.point,width,depth);
        } else throw new RuntimeException("unknown move type for gtp coordinates: "+move);
    }
    public static Move2 fromGTP(Stone color,String raw,int width,int depth) {
        if(raw==null) return Move2.nullMove;
        String string=raw.trim();
        if(string.equals("")) {
            System.out.println("string is: "+"\"\"");
            return Move2.nullMove; // pass?
        } else if(string.equalsIgnoreCase(gtpPassString)) {
            if(color.equals(Stone.black)) return Move2.blackPass;
            else if(color.equals(Stone.white)) return Move2.whitePass;
            else throw new RuntimeException("pass bad color!");
        } else if(string.equalsIgnoreCase(gtpResignString)) {
            if(color.equals(Stone.black)) return Move2.blackResign;
            else if(color.equals(Stone.white)) return Move2.whiteResign;
            else throw new RuntimeException("resign bad color!");
        }
        Point point=Coordinates.fromGtpCoordinateSystem(string,width);
        // check to see if point is on board? or at lease in range?
        return new Move2(MoveType.move,color,point);
    }
    public static List<String> toGTPMoves(List<Move2> moves,int width,int depth) {
        List<String> commands=new ArrayList<>();
        for(Move2 move:moves) { // how about pass and resign?
            String string=Command.play.name()+" "+move.color+" "+toGTPCoordinates(move,width,depth);
            commands.add(string);
        }
        return commands;
    }
    public static String gtpPassString="pass",gtpResignString="resign";
}
