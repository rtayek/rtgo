package model;
import static model.MoveHelper.*;
import java.util.*;
import controller.Command;
import equipment.*;
import model.Move.*;
public class MoveHelper {
    public static String toSGFCoordinates(Move move,int width,int depth) {
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
    public static String toGTPCoordinates(Move move,int width,int depth) {
        if(move instanceof Pass) {
            return move.color().equals(Stone.black)?Move.blackPass.name():Move.whitePass.name();
        } else if(move instanceof Resign) {
            return move.color().equals(Stone.black)?Move.blackResign.name():Move.whiteResign.name();
        } else if(move instanceof Null) {
            throw new RuntimeException("null move has no gtp coordinates!");
        } else if(move instanceof MoveImpl) {
            return Coordinates.toGtpCoordinateSystem(move.point(),width,depth);
        } else throw new RuntimeException("unknown move type for gtp coordinates: "+move);
    }
    public static Move fromGTP(Stone color,String string,int width,int depth) {
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
    public static List<String> toGTPMoves(List<Move> moves,int width,int depth) {
        List<String> commands=new ArrayList<>();
        for(Move move:moves) { // how about pass and resign?
            String string=Command.play.name()+" "+move.color()+" "+move.toGTPCoordinates(width,depth);
            commands.add(string);
        }
        return commands;
    }
    public static String gtpPassString="pass",gtpResignString="resign";
}
