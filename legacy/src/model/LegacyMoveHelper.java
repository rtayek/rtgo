package model;
import equipment.*;
import model.LegacyMove.*;
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
	public static String gtpPassString="pass",gtpResignString="resign";
}
